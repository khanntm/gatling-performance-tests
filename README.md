# Gatling Maven Demo - VideoGameDb Performance Tests

This project is a **Gatling performance testing framework** built using **Java DSL** and **Maven**.  
It simulates load tests against the `VideoGameDb` API with different scenarios such as CRUD operations, authentication, and random weighted execution.

**Run a specific simulation**
mvn gatling:test -Dgatling.simulationClass=videogamedb.finalSimulation.VideoGameDbFullTest

🛠 Features

Feeders (CSV, JSON) for dynamic test data

Authentication flow with token handling

CRUD operations (Create, Read, Update, Delete)

RandomSwitch scenarios for weighted execution

Pauses for more realistic user behavior

Maven + Gatling plugin integration

Reports automatically generated after test execution

⚙️ Configuration

Simulation Parameters (e.g., USER_COUNT, TEST_DURATION, BASE_REQUESTS)
can be defined inside the simulation class or externalized via properties.

✅ Requirements

Java 17+

Maven 3.8+

Gatling Maven Plugin
