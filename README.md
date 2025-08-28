## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |

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
