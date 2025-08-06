package videogamedb.feeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbFeeders extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk:443/api/")
            .acceptHeader("application/json");

    private static FeederBuilder.FileBased<String> csvFeeder = csv("data/gamecsvFile.csv").circular();


    private static ChainBuilder getSpecificGame =
            feed(csvFeeder)
                    .exec(http("Get video game with name - #{gameName}")
                    .get("/videogame/#{gameId}")
                            .check(jmesPath("name").isEL("#{gameName}"))
                            .check(status().is(200)));

    private ScenarioBuilder scn = scenario("Video Game Db - Section 6 code")
            .repeat(10).on(
                    exec(getSpecificGame)
                            .pause(1)
            );
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
}
