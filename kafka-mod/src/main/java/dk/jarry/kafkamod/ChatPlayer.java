package dk.jarry.kafkamod;

import net.minecraft.server.level.ServerPlayer;

public class ChatPlayer {

    ServerPlayer player;

    public ChatPlayer(ServerPlayer player){
        this.player = player;
    }

    public double getX(){
        return player.getX();
    }

    public double getY(){
        return player.getY();
    }

    public double getZ(){
        return player.getZ();
    }

    public String getIpAddress(){
        return player.getIpAddress();
    }

}
