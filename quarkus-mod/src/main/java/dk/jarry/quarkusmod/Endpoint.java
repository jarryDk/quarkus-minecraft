package dk.jarry.quarkusmod;

import org.jetbrains.annotations.NotNull;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Path("/observability")
public class Endpoint {

    // Ugly and static, but it will work for the moment
    private static Object player;

    @POST
    @Path("/log")
    @Consumes("text/plain")
    public String log(String message) {
        System.out.println("[Quarkcraft] log");
        return invokeOnPlayer("say", message);
    }

    @GET
    @Path("/event")
    public String alert() {
        System.out.println("[Quarkcraft] event");
        return invokeOnPlayer("event", "A thing happened out in the real world");
    }

    @GET
    @Path("/boom")
    public String explode() {
        System.out.println("[Quarkcraft] boom");
        return invokeOnPlayer("explode", "Something -bad- happened out in the real world");
    }

    @NotNull
    private String invokeOnPlayer(String methodName, String message) {
        if (player != null) {
            // The player will be in a different classloader to us, so we need to use more reflection
            try {
                // Cheerfully assume all methods on PlayerWrapper take a string as an argument
                Method m = player.getClass().getMethod(methodName, String.class);
                m.invoke(player, message);
                return "minecraft world updated with " + methodName;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                return "internal error";
            }
        } else {
            return "no player logged in";
        }
    }

    public static void setPlayer(Object newPlayer) {
        player = newPlayer;
    }


}
