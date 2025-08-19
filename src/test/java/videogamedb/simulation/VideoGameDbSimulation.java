package videogamedb.simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbSimulation extends Simulation {

    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://www.videogamedb.uk:443/api/")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS_COUNT","5"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION","10"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION","60"));

    @Override
    public void before() {
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping user over %d seconds%n", RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds", TEST_DURATION);
    }

    private static ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame"));

    private static ChainBuilder getSpecificGame =
            exec(http("Get Specific Game")
                    .get("/videogame/2"));

    private ScenarioBuilder scn = scenario("Video game db - Section 7 code")
            .forever().on(
                    exec(getAllVideoGames)
            .pause(5)
            .exec(getSpecificGame)
            .pause(2)
            .exec(getAllVideoGames)
            );

    {
        setUp(
                scn.injectOpen(
                        nothingFor(5),
                        //atOnceUsers(10),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)

                ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }
}
