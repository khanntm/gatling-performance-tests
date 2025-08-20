package videogamedb.finalSimulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.recorder.internal.bouncycastle.oer.OERDefinition.choice;

public class VideoGameDbFullTest extends Simulation {

    // HTTP PROTOCOL
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // RUNTIME PARAMETERS
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("USERS", "5"));
    private static final int RAMP_DURATION = Integer.parseInt(System.getProperty("RAMP_DURATION", "20"));
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("TEST_DURATION", "30"));

    // FEEDER
    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").random();

    //BEFORE BLOCK
    @Override
    public void before() {
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Ramping users over %d seconds%n", RAMP_DURATION);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    // HTTP CALLS
    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"admin\",\n" +
                            "  \"username\": \"admin\"\n" +
                            "}"))
                    .check(status().is(200))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame")
                    .check(status().is(200)));

    private static ChainBuilder createNewGame =
            feed(jsonFeeder)
                    .exec(http("Create New Game - #{name} - #{id}")
                            .post("/videogame")
                            .header("Authorization", "Bearer #{jwtToken}")
                            .body(ElFileBody("body/newGameTemplate.json")).asJson()
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().is(201)))

                    .exec(
                            session -> {
                                System.out.println(session);
                                System.out.println("Response Body Create New Game: " + session.getString("responseBody"));
                                return session;
                            }

                    );

    private static ChainBuilder getLastPostedGame =
            exec(http("Get Last Posted Game - #{name}")
                    .get("/videogame/#{id}")
                    .check(status().is(200))
                    .check(jmesPath("name").isEL("#{name}")));

    private static ChainBuilder deleteLastPostedGame =
            exec(http("Delete Game - #{name}")
                    .delete("/videogame/#{id}")
                    .header("Authorization", "Bearer #{jwtToken}")
                    .check(status().is(200))
                    .check(bodyString().is("Video game deleted")));


    // SCENARIO: mỗi user chọn ngẫu nhiên flow
    private ScenarioBuilder scn = scenario("Video game db - realistic load")
            .forever().on(
                    exec(getAllVideoGames)
                            .pause(2)
                            .exec(authenticate)
                            .pause(2)
                            .exec(createNewGame)
                            .pause(2)
                            .exec(getLastPostedGame)
                            .pause(2)
                            .exec(deleteLastPostedGame)
            );

    // LOAD SIMULATION
    {
        setUp(
                scn.injectOpen(
                        nothingFor(3),
                        rampUsers(USER_COUNT).during(RAMP_DURATION)
                ).protocols(httpProtocol)
        ).maxDuration(TEST_DURATION);
    }


    @Override
    public void after() {
        System.out.println("Test completed");
    }


}
