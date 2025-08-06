package videogamedb.scriptfundamentals;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;


public class MyFirstTest extends Simulation {

        // 1. Http Config
        private HttpProtocolBuilder httpProtocol = http
                .baseUrl("https://www.videogamedb.uk:443/api/")
                .acceptHeader("application/json");

        // 2. Scenario Def
        private ScenarioBuilder scn = scenario("My Fisrt Test")
                .exec(http("Get all games")
                                .get("/videogame"));

        // 3. Load Simulation
        {
            setUp(
                    scn.injectOpen(atOnceUsers(1))
            ).protocols(httpProtocol);

        }
}
