package videogamedb.finalSimulation;

import io.gatling.javaapi.core.Simulation;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbFullTest_randomSwitch extends Simulation {

    // HTTP PROTOCOL
    private HttpProtocolBuilder httpProtocol = http
            .baseUrl("https://videogamedb.uk/api")
            .acceptHeader("application/json")
            .contentTypeHeader("application/json");

    // ---- Config từ System properties ----
    private static final int TEST_DURATION = Integer.parseInt(System.getProperty("DURATION", "60")); // seconds
    private static final int BASE_REQUESTS = Integer.parseInt(System.getProperty("BASE_REQUESTS", "500")); // total cho GetAllGames
    private static final int USER_COUNT = Integer.parseInt(System.getProperty("CONCURRENCE_USERS","5"));
    // % for other APIs
    private static final double AUTH_PERCENT       = Double.parseDouble(System.getProperty("AUTH_PERCENT", "20"));   // 20%
    private static final double CREATE_PERCENT     = Double.parseDouble(System.getProperty("CREATE_PERCENT", "15")); // 15%
    private static final double GET_LAST_PERCENT   = Double.parseDouble(System.getProperty("GET_LAST_PERCENT", "10"));// 10%
    private static final double DELETE_PERCENT     = Double.parseDouble(System.getProperty("DELETE_PERCENT", "5"));  // 5%


    // FEEDER
    private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").random();

    //BEFORE BLOCK
    @Override
    public void before() {
        System.out.printf("Running test with %d users%n", USER_COUNT);
        System.out.printf("Total test duration: %d seconds%n", TEST_DURATION);
    }

    // HTTP CALLS
    private static ChainBuilder getAllVideoGames =
            exec(http("Get all video games")
                    .get("/videogame")
                    .check(status().is(200)));

    private static ChainBuilder authenticate =
            exec(http("Authenticate")
                    .post("/authenticate")
                    .body(StringBody("{\n" +
                            "  \"password\": \"admin\",\n" +
                            "  \"username\": \"admin\"\n" +
                            "}"))
                    .check(status().is(200))
                    .check(jmesPath("token").saveAs("jwtToken")));

    private static ChainBuilder createNewGame =
            feed(jsonFeeder)
                    .exec(http("Create New Game - #{name} - #{id}")
                            .post("/videogame")
                            .header("Authorization","Bearer #{jwtToken}")
                            .body(ElFileBody("body/newGameTemplate.json")).asJson()
                            .check(bodyString().saveAs("responseBody"))
                            .check(status().in(200, 201)))

                    .exec(
                            session -> {
                                System.out.println(session);
                                System.out.println("Response Body Create New Game " + session.getString("responseBody"));
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

    // SCENARIO: randomSwitch (Percent Execution)
    private final ScenarioBuilder getAllScenario = scenario("GetAll")
            .exec(getAllVideoGames);

    // USE FOR TEST CASE UN-DEPENDENCIES
    /*
    private final ScenarioBuilder othersScenario = scenario("Others")
            .exec(authenticate)
            .randomSwitch().on(
                    new Choice.WithWeight(CREATE_PERCENT, exec(createNewGame)),
                    new Choice.WithWeight(GET_LAST_PERCENT, exec(getLastPostedGame)),
                    new Choice.WithWeight(DELETE_PERCENT, exec(deleteLastPostedGame))
            ); */

    // CRUD Flow to ensure not failed
    private final ScenarioBuilder othersScenario =scenario("Others")
                    .exec(authenticate) // get token
                    .pause(3)

                    .exec(createNewGame) // create new
                    .pause(2)

                    .exec(getLastPostedGame) // get id
                    .pause(2)

                    .exec(deleteLastPostedGame); // delete by id

    {
        setUp(
                // Throughput BASE_REQUEST for GetAllGame
                getAllScenario.injectOpen(
                    rampUsers(BASE_REQUESTS).during(TEST_DURATION)//  each user 1 request reach to BASE_REQUESTS in TEST_DURATION
                ),

                // others run during TEST_DURATION
                othersScenario.injectOpen(constantUsersPerSec(USER_COUNT).during(TEST_DURATION)

                )
        ).protocols(httpProtocol);

    }
}



