package dk.jarry.quarkus.minecraft.extension.it;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class QuarkusMinecraftExtensionResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/quarkus-minecraft-extension")
                .then()
                .statusCode(200)
                .body(is("Hello quarkus-minecraft-extension"));
    }
}
