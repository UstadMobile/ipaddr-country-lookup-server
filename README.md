IP Address Country Lookup Server

A self-hosted server that returns the country for a given IP address or domain name. The response 
format matches the public ip-api.com API, so this server can be used as a self-hosted alternative.

Country data is resolved locally using the MaxMind GeoLite2 Country database.

## Building & Running

To build or run the project, use one of the following tasks:

| Task                          | Description                                                          |
| -------------------------------|---------------------------------------------------------------------- |
| `./gradlew test`              | Run the tests                                                        |
| `./gradlew build`             | Build everything                                                     |

## Setup GeoLite2 Database

 - Create MaxMind Account (free): https://www.maxmind.com/en/geolite2/signup
 - Download Database: Get GeoLite2-Country.mmdb in binary format 
 - Make the database accessible to the app in any of the following ways:
   - Put the GeoLite2-Country.mmdb file in the working directory
   - Set the environment variable `GEO_DATABASE_PATH` to the path of the GeoLite2-Country.mmdb file
   - Set the `geo.database.path` property in the application.conf file to the path of the GeoLite2-Country.mmdb file 

## Running

From source:

` ./gradlew run`

Using a distribution:


Then use any api client to do an IP address GeoLookup:

e.g.
```shell
curl http://localhost:8080/json/google.com
{
    "status": "success",
    "countryCode": "US",
    "country": "United States",
    "query": "google.com"
}
```

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

This product uses GeoLite2 data created by MaxMind, available from [https://www.maxmind.com](https://www.maxmind.com).
You must download the GeoLite IP address database yourself and accept/abide by the GeoLite End User
License Agreement.

