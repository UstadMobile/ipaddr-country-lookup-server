# ipaddr-country-lookup-server

This project was created using the [Ktor Project Generator](https://start.ktor.io).

Here are some useful links to get you started:

- [Ktor Documentation](https://ktor.io/docs/home.html)
- [Ktor GitHub page](https://github.com/ktorio/ktor)
- The [Ktor Slack chat](https://app.slack.com/client/T09229ZC6/C0A974TJ9). You'll need to [request an invite](https://surveys.jetbrains.com/s3/kotlin-slack-sign-up) to join.

## Features

Here's a list of features included in this project:

| Name                                                               | Description                                                                        |
| --------------------------------------------------------------------|------------------------------------------------------------------------------------ |
| [Caching Headers](https://start.ktor.io/p/caching-headers)         | Provides options for responding with standard cache-control headers                |
| [Default Headers](https://start.ktor.io/p/default-headers)         | Adds a default set of headers to HTTP responses                                    |
| [CORS](https://start.ktor.io/p/cors)                               | Enables Cross-Origin Resource Sharing (CORS)                                       |
| [Content Negotiation](https://start.ktor.io/p/content-negotiation) | Provides automatic content conversion according to Content-Type and Accept headers |
| [Routing](https://start.ktor.io/p/routing)                         | Provides a structured routing DSL                                                  |
| [Jackson](https://start.ktor.io/p/ktor-jackson)                    | Handles JSON serialization using Jackson library                                   |

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |
| `buildFatJar`                 | Build an executable JAR of the server with all dependencies included |

## Set Up GeoLite2 Database

 - Create MaxMind Account (free): https://www.maxmind.com/en/geolite2/signup
 - Download Database: Get GeoLite2-Country.mmdb in binary format
 - Set the environment variable

 export GEO_DATABASE_PATH=/path/to/GeoLite2-Country.mmdb

 Then,

 ./gradlew run

## Test the Endpoint

curl -H "X-Forwarded-For: 8.8.8.8" http://localhost:8080/country


```
2024-12-04 14:32:45.584 [main] INFO  Application - Application started in 0.303 seconds.
2024-12-04 14:32:45.682 [main] INFO  Application - Responding at http://0.0.0.0:8080
```
## License

This project is licensed under the MIT License.

## Attribution

This product includes GeoLite2 data created by MaxMind, available from [https://www.maxmind.com](https://www.maxmind.com).
