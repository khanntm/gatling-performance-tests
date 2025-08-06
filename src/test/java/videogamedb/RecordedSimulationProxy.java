package videogamedb;

import java.time.Duration;
import java.util.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;
import io.gatling.javaapi.jdbc.*;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;
import static io.gatling.javaapi.jdbc.JdbcDsl.*;

public class RecordedSimulationProxy extends Simulation {

  private HttpProtocolBuilder httpProtocol = http
    .baseUrl("https://videogamedb.uk")
    .inferHtmlResources(AllowList(), DenyList(".*\\.js", ".*\\.css", ".*\\.gif", ".*\\.jpeg", ".*\\.jpg", ".*\\.ico", ".*\\.woff", ".*\\.woff2", ".*\\.(t|o)tf", ".*\\.png", ".*\\.svg", ".*detectportal\\.firefox\\.com.*"))
    .acceptHeader("*/*")
    .acceptEncodingHeader("gzip, deflate, br")
    .userAgentHeader("PostmanRuntime/7.44.1");
  
  private Map<CharSequence, String> headers_0 = Map.of("Postman-Token", "65fdc300-25e4-4699-bc6d-eecee034be24");
  
  private Map<CharSequence, String> headers_1 = Map.of("Postman-Token", "6e9c3f06-28e7-465d-92a9-5b97a87fa67f");
  
  private Map<CharSequence, String> headers_2 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Postman-Token", "f5687641-ae9f-4a13-8ced-c1ed0e3fddf3")
  );
  
  private Map<CharSequence, String> headers_3 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Postman-Token", "b5c1db2f-f266-440b-bae1-bd87cf4576f6"),
    Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1Mzk3MDMxNywiZXhwIjoxNzUzOTczOTE3fQ.jNI60bBkq4whF0VBOZmjUtJ6RRJHAkFxrOkC_xZU9DQ")
  );
  
  private Map<CharSequence, String> headers_4 = Map.ofEntries(
    Map.entry("Content-Type", "application/json"),
    Map.entry("Postman-Token", "4c5587cf-8ad7-446c-97c3-ced2df97d4da"),
    Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1Mzk3MDMxNywiZXhwIjoxNzUzOTczOTE3fQ.jNI60bBkq4whF0VBOZmjUtJ6RRJHAkFxrOkC_xZU9DQ")
  );
  
  private Map<CharSequence, String> headers_5 = Map.ofEntries(
    Map.entry("Postman-Token", "e928ad18-98ab-4b04-9dc3-5c93a85d25d0"),
    Map.entry("authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbiIsImlhdCI6MTc1Mzk3MDMxNywiZXhwIjoxNzUzOTczOTE3fQ.jNI60bBkq4whF0VBOZmjUtJ6RRJHAkFxrOkC_xZU9DQ")
  );


  private ScenarioBuilder scn = scenario("RecordedSimulationProxy")
    .exec(
      http("request_0")
        .get("/api/videogame")
        .headers(headers_0),
      pause(102),
      http("request_1")
        .get("/api/videogame/2")
        .headers(headers_1),
      pause(19),
      http("request_2")
        .post("/api/authenticate")
        .headers(headers_2)
        .body(RawFileBody("videogamedb/recordedsimulationproxy/0002_request.json")),
      pause(42),
      http("request_3")
        .post("/api/videogame")
        .headers(headers_3)
        .body(RawFileBody("videogamedb/recordedsimulationproxy/0003_request.json")),
      pause(40),
      http("request_4")
        .put("/api/videogame/3")
        .headers(headers_4)
        .body(RawFileBody("videogamedb/recordedsimulationproxy/0004_request.json")),
      pause(15),
      http("request_5")
        .delete("/api/videogame/2")
        .headers(headers_5)
    );

  {
	  setUp(scn.injectOpen(atOnceUsers(1))).protocols(httpProtocol);
  }
}
