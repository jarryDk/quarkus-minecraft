package dk.jarry.quarkusmod;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

/**
 * We need a wrapper here because we need something with a simple enough
 * signature that everything in it is in a JVM library, rather than loaded
 * by one of the fragmented classloaders. That allows us to find and
 * invoke the method by reflection.
 */
public class PlayerWrapper {

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private final Player player;

    PlayerWrapper(Player player) {
        this.player = player;
    }

    public void event(String message) {

        Vec3 pos = getPositionInFrontOfPlayer(3);

        player.displayClientMessage( Component.literal(message), true);

        player.displayClientMessage( Component.literal(message), true);

        Level world = player.getCommandSenderWorld();

        LightningBolt lightning = EntityType.LIGHTNING_BOLT.create(world);
        lightning.setPos(pos);
        lightning.setVisualOnly(true);
        world.addFreshEntity(lightning);

        Chicken chicken = EntityType.CHICKEN.create(world);
        chicken.setPos(pos);
        String time = DATE_FORMAT.format(new Date());
        Component timeComponent = Component.literal(time);
        chicken.setCustomName(timeComponent);
        chicken.setCustomNameVisible(true);
        world.addFreshEntity(chicken);

    }

    public void say(String message) {
        // Use the chat interface for logs since it wraps more nicely
        Component msg = Component.literal(message);
        player.sendSystemMessage(msg);
    }

    public void explode(String message) {
        player.displayClientMessage(Component.literal(message), true);
        Level level = player.getCommandSenderWorld();
        Chicken chicken = EntityType.CHICKEN.create(level);
        chicken.setPos(getPositionInFrontOfPlayer(6));
        level.addFreshEntity(chicken);

        List<BlockPos> affectedPositions = new ArrayList<>();
        affectedPositions.add(new BlockPos(chicken.getX(), chicken.getY(),
                chicken.getZ()));
        Explosion explosion = new Explosion(level, chicken, chicken.getX(), chicken.getY(),
                chicken.getZ(), 6F, affectedPositions);

        explosion.explode();
    }

    @NotNull
    private Vec3 getPositionInFrontOfPlayer(int distance) {
        double x = player.getX() + distance * player.getLookAngle().x;
        double y = player.getY() + distance * player.getLookAngle().y;
        double z = player.getZ() + distance * player.getLookAngle().z;
        Vec3 pos = new Vec3(x, y, z);
        return pos;
    }

}
