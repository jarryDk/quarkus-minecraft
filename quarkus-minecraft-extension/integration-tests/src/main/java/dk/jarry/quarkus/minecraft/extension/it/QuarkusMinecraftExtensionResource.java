package dk.jarry.quarkus.minecraft.extension.it;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/quarkus-minecraft-extension")
@ApplicationScoped
public class QuarkusMinecraftExtensionResource {
    // add some rest methods here

    @GET
    public String hello() {
        return "Hello quarkus-minecraft-extension";
    }
}
