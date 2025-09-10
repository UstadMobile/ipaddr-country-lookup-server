package com.ustadmobile.countrylookupserver.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import com.ustadmobile.countrylookupserver.service.GeoIpService
import io.ktor.http.HttpStatusCode
import org.slf4j.LoggerFactory

fun Route.configureGeoIpRoutes(geoIpService: GeoIpService) {
    val logger = LoggerFactory.getLogger("GeoIpRoutes")

    get(COUNTRY_ENDPOINT_PATH) {
        val clientIp = getClientIpAddress(call)
        logger.debug("Processing request for IP: $clientIp")

        try {
            val countryResponse = geoIpService.getCountryForIp(clientIp)
            if (countryResponse != null) {
                call.respond(countryResponse)
            } else {
                call.respond(HttpStatusCode.NotFound, "Could not determine country for IP: $clientIp")
            }
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid IP address request: $clientIp - ${e.message}")
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Invalid IP address")
        } catch (e: RuntimeException) {
            logger.error("Runtime error processing IP: $clientIp", e)
            call.respond(HttpStatusCode.InternalServerError, e.message ?: "Internal server error")
        } catch (e: Exception) {
            logger.error("Unexpected error processing IP: $clientIp", e)
            call.respond(HttpStatusCode.InternalServerError, "Internal server error")
        }
    }
}
private fun getClientIpAddress(call: ApplicationCall): String {
    val xForwardedFor = call.request.header(X_FORWARDED_FOR_HEADER)
    if (xForwardedFor != null) {
        val firstIp = xForwardedFor.split(",").firstOrNull()?.trim()
        if (firstIp != null && firstIp.isNotBlank()) {
            return firstIp
        }
    }

    val xRealIp = call.request.header(X_REAL_IP_HEADER)
    if (xRealIp != null && xRealIp.isNotBlank()) {
        return xRealIp
    }

    return call.request.local.remoteHost
}

private const val COUNTRY_ENDPOINT_PATH = "/country"
private const val X_FORWARDED_FOR_HEADER = "X-Forwarded-For"
private const val X_REAL_IP_HEADER = "X-Real-IP"