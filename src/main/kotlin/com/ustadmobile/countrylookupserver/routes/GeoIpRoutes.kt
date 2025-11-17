package com.ustadmobile.countrylookupserver.routes

import com.ustadmobile.countrylookupserver.data.CountryResponse
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import com.ustadmobile.countrylookupserver.service.GeoIpService
import io.ktor.http.HttpStatusCode
import org.slf4j.LoggerFactory

fun Route.configureGeoIpRoutes(geoIpService: GeoIpService) {
    val logger = LoggerFactory.getLogger("GeoIpRoutes")

    get("/api/country/{host}") {
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
}