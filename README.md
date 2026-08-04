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
 
## API Reference

This server implements the same response format as the public [ip-api.com](https://ip-api.com) API.

### Endpoint
GET /json/{host}

Where `{host}` is an IP address or domain name.

### Reference
- Public API this format is based on: https://ip-api.com/docs/api:json

## License

This project is licensed under the MIT License.

## Attribution

This product includes GeoLite2 data created by MaxMind, available from [https://www.maxmind.com](https://www.maxmind.com).
