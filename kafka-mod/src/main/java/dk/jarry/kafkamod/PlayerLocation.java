package dk.jarry.kafkamod;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class PlayerLocation {

    Player player;

    public PlayerLocation(Player player){
        this.player = player;
    }

    public double getX() {
        return player.getX();
    }

    public double getY() {
        return player.getY();
    }

    public double getZ() {
        return player.getZ();
    }

    public Component getName() {
        return player.getName();
    }

    public String toString() {
        return "Player : " + getName().getString() + " x=" + player.getX() + " y=" + player.getY() + " z=" + player.getZ();
    }

}
