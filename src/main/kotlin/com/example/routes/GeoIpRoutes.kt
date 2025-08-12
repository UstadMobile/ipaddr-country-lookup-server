package com.example.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.request.*
import com.example.service.GeoIpService
import com.example.usecase.GetCountryForIpUseCase

fun Route.configureGeoIpRoutes() {
    val geoIpService = GeoIpService()
    val getCountryForIpUseCase = GetCountryForIpUseCase(geoIpService)

    get(COUNTRY_ENDPOINT_PATH) {
        val clientIp = getClientIpAddress(call)
        val countryResponse = getCountryForIpUseCase.execute(clientIp)
        call.respond(countryResponse)
    }
}

private fun getClientIpAddress(call: ApplicationCall): String {
    // Check for X-Forwarded-For header (common in load balancer setups)
    val xForwardedFor = call.request.header(X_FORWARDED_FOR_HEADER)
    if (xForwardedFor != null) {
        val firstIp = xForwardedFor.split(",").firstOrNull()?.trim()
        if (firstIp != null && firstIp.isNotBlank()) {
            return firstIp
        }
    }

    // Check for X-Real-IP header (common in nginx setups)
    val xRealIp = call.request.header(X_REAL_IP_HEADER)
    if (xRealIp != null && xRealIp.isNotBlank()) {
        return xRealIp
    }

    // Fallback to remote host from the call
    return call.request.local.remoteHost
}

private const val COUNTRY_ENDPOINT_PATH = "/country"
private const val X_FORWARDED_FOR_HEADER = "X-Forwarded-For"
private const val X_REAL_IP_HEADER = "X-Real-IP"
