package com.ustadmobile.countrylookupserver.routes

import com.ustadmobile.countrylookupserver.data.CountryResponse
import com.ustadmobile.countrylookupserver.service.GeoIpService
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.slf4j.LoggerFactory

/**
 * Configures the GeoIP lookup routes.
 *
 * Provides `GET /json/{host}` where `{host}` is an IP address
 * (e.g. 8.8.8.8) or domain name (e.g. google.com).
 *
 * Response format matches the public ip-api.com API so this server
 * can be used as a self-hosted alternative.
 *
 * @see <a href="https://ip-api.com/docs/api:json">ip-api.com JSON API</a>
 */
fun Route.configureGeoIpRoutes(geoIpService: GeoIpService) {
    val logger = LoggerFactory.getLogger("GeoIpRoutes")

    get("/json/{host}") {
        val host = call.parameters["host"]
        logger.debug("Processing request for host: $host")

        if (host.isNullOrBlank()) {
            call.respond(
                HttpStatusCode.BadRequest,
                CountryResponse(
                    status = "fail",
                    message = "Invalid query",
                    query = ""
                )
            )
            return@get
        }

        try {
            val countryResponse = geoIpService.getCountryForIp(host)
            call.response.header(HttpHeaders.CacheControl, "max-age=$CACHE_MAX_AGE_SECONDS")
            call.respond(countryResponse)
        } catch (e: Exception) {
            logger.error("Error processing host: $host", e)
            call.respond(
                CountryResponse(
                    status = "fail",
                    message = "query failed",
                    query = host
                )
            )
        }
    }

    get("/json/") {
        call.respond(
            HttpStatusCode.BadRequest,
            CountryResponse(
                status = "fail",
                message = "Invalid query",
                query = ""
            )
        )
    }
}

/** One day. A server's country changes very rarely, so responses can be cached for a long time. */
private const val CACHE_MAX_AGE_SECONDS = 86_400