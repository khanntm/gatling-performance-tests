package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.List;

public class VideoGameDb extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk:443/api/")
            .acceptHeader("application/json");

    // Refactor Gatling Coding
    private static ChainBuilder getAllVideoGame =
            repeat(5) .on(exec(http("Get all video games")
                    .get("/videogame")
                    .check(status().not(400),status().not(500))
            .check(jmesPath("[1].id").saveAs("gameId")))
            );

    private static ChainBuilder getSpecificVideoGame =
            repeat(5).on(exec(http("Get specific video game")
                    .get("/videogame/#{gameId}")
                    .check(status().in(200, 201, 202))
                    .check(jmesPath("name").is("Gran Turismo 3")))
            );



    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game Db - Section 5 code")

            .exec(getAllVideoGame)
                    //.check(jsonPath("$[?(@.id==1)].name").is("Resident Evil 4"))
            .pause(5)
            .exec(getSpecificVideoGame)
            .pause(1, 5)
            .exec(getAllVideoGame)
            .pause(Duration.ofMillis(4000));

    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }





}
