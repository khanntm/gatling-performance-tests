package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import java.time.Duration;
import java.util.List;
public class VideoGameDbData extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk:443/api/")
            .acceptHeader("application/json");

    // Scenario Definition
    private ScenarioBuilder scn = scenario("Video Game Db - Section 5 code")

            .exec(http("Get specific game")
                    .get("/videogame/1")
                    .check(status().in(200, 201, 202))
                    .check(jmesPath("name").is("Resident Evil 4")))
            .pause(1, 5)

            .exec(http("Get all video game")
                    .get("/videogame")
                    .check(status().not(400), status().not(500))
                    .check(jmesPath("[1].id").saveAs("gameId")))
            .pause(Duration.ofMillis(4000))

            //For debugging
            .exec(
                    session -> {
                        System.out.println(session);
                        System.out.println("gameId set to: " + session.getString("gameId"));
                        return session;
                    }
                    )

            .exec(http("Get specific game with gameId")
                            .get("/videogame/#{gameId}")
                            .check(status().in(200, 201, 202))
                    .check(jmesPath("name").is("Gran Turismo 3"))
                    .check(bodyString().saveAs("responseBody")))


            .exec(
                            session -> {
                                System.out.println(session);
                                System.out.println("Response Body: " + session.getString("responseBody"));
                                return session;
                            }

            );


    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }

}