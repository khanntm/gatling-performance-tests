package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.List;

public class CreateVideoGame extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk:443/api/")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"admin\",\n" +
                            "  \"username\": \"admin\"\n" +
                            "}"))
                    .check(jmesPath("token").saveAs("jwtToken")));


    private static ChainBuilder createNewGame =
            exec(http("Create new game")
                    .post("/videogame")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .body(StringBody("{\n" +
                            "  \"category\": \"Platform\",\n" +
                            "  \"name\": \"Mario\",\n" +
                            "  \"rating\": \"Mature\",\n" +
                            "  \"releaseDate\": \"2012-05-04\",\n" +
                            "  \"reviewScore\": 85\n" +
                            "}")).check(status().is(200))

            )

                    .exec(
                            session -> {
                                System.out.println(session);
                                System.out.println("Response Body: " + session.getString("responseBody"));
                                return session;
                            }

                    );

    private ScenarioBuilder scn = scenario("Create video")
            .exec(authenticate)
            .exec(createNewGame);
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
