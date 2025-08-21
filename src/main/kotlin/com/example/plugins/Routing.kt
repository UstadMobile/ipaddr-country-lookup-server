package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.configureGeoIpRoutes
import com.example.service.GeoIpService

fun Application.configureRouting(geoIpService: GeoIpService) {
    routing {
        configureGeoIpRoutes(geoIpService)
    }
}