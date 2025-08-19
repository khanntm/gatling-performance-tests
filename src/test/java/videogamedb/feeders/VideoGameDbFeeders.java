package videogamedb.feeders;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import jodd.util.RandomString;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

public class VideoGameDbFeeders extends Simulation {

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

    //read csv file
    //private static FeederBuilder.FileBased<String> csvFeeder = csv("data/gamecsvFile.csv").circular();

    //read json file
    //private static FeederBuilder.FileBased<Object> jsonFeeder = jsonFile("data/gameJsonFile.json").circular();

    //get csv file
    /* private static ChainBuilder getSpecificGame =
            feed(csvFeeder)
                    .exec(http("Get video game with name - #{gameName}")
                    .get("/videogame/#{gameId}")
                            .check(jmesPath("name").isEL("#{gameName}"))
                            .check(status().is(200))); */

    //Custom Feeder
   private static Iterator<Map<String, Object>> customFeeder =
            Stream.generate((Supplier<Map<String, Object>>) () -> {
                Random rand = new Random();
                int gameId = rand.nextInt(10 - 1 + 1) +1;
                //return Collections.singletonMap("gameId", gameId);

                String gameName = RandomStringUtils.randomAlphabetic(5) + "-gameName";
                String releaseDate = randomDate().toString();
                int reviewScore = rand.nextInt(100);
                String category = RandomStringUtils.randomAlphabetic(5) + "-category";
                String rating = RandomStringUtils.randomAlphabetic(4) + "-rating";

                HashMap<String, Object> hmap = new HashMap<String, Object>();
                hmap.put("gameId", gameId);
                hmap.put("gameName",gameName);
                hmap.put("releaseDate", releaseDate);
                hmap.put("reviewScore", reviewScore);
                hmap.put("category",category);
                hmap.put("rating", rating);
                return hmap;
            }

            ).iterator();





    // Create new game using Custom Feeder
        private static ChainBuilder createNewGame =
                feed(customFeeder)
                        .exec(http("Create new game")
                        .post("/videogame")
                                        .header("authorization", "Bearer #{jwtToken}")

                        /*.body(StringBody("{\n" +
                                "  \"category\": \"Platform\",\n" +
                                "  \"name\": \"Mario\",\n" +
                                "  \"rating\": \"Mature\",\n" +
                                "  \"releaseDate\": \"2012-05-04\",\n" +
                                "  \"reviewScore\": 85\n" +
                                "}")).check(status().is(200) */
                                .body(ElFileBody("body/newGameTemplate.json")).asJson()
                                .check(bodyString().saveAs("responseBody"))
                                .check(status().is(200)))

                        .exec(
                                session -> {
                                     System.out.println(session);
                                    System.out.println("Response Body: " + session.getString("responseBody"));
                                    return session;
                                }

                        );

    //json call
    /* private static ChainBuilder getSpecificGame
            feed(jsonFeeder)
                    .exec(http("Get video game with name - #{name}")
                            .get("/videogame/#{id}")
                            .check(jmesPath("name").isEL("#{name}"))
                            .check(status().is(200))); */

    //Complex Custom Feeder
        public static LocalDate randomDate() {
            int hundredYears = 100 * 365;
            return LocalDate.ofEpochDay(ThreadLocalRandom.current().nextInt(-hundredYears, hundredYears));


        }


    private static ChainBuilder getSpecificGame =
            feed(customFeeder)
                    .exec(http("Get video game with name - #{gameId}")
                            .get("/videogame/#{gameId}")
                            //.check(jmesPath("name").isEL("#{name}"))
                            .check(status().is(200)));

    /* private ScenarioBuilder scn = scenario("Video Game Db - Section 6 code")
            .repeat(10).on(
                    exec(getSpecificGame)
                            .pause(1)
                            //.exec(authenticate)
                            //.exec(createNewGame)
            );
    {
        setUp(
                scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
} */

private ScenarioBuilder scn = scenario("Create video")
        .exec(authenticate)
        .exec(createNewGame);
    {
setUp(
        scn.injectOpen(atOnceUsers(1))
        ).protocols(httpProtocol);
    }
            }