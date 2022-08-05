package dk.jarry.kafkamod;

import net.minecraft.server.level.ServerPlayer;

public class ChatRecord {
    
    public String message;
    public String username;
    public ChatPlayer player;

    public ChatRecord(String message, String username){
        this.message = message;
        this.username = username;
    }

    public ChatRecord(String message, String username, ChatPlayer player){
        this.message = message;
        this.username = username;
        this.player = player;
    }

    public String getMessage() {
        return message;
    }

    public String getUsername() {
        return username;
    }

    public ChatPlayer getPlayer() {
        return player;
    }
       
}
