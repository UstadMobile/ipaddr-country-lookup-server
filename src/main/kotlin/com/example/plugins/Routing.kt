package com.example.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.example.routes.configureGeoIpRoutes

fun Application.configureRouting() {
    routing {
        configureGeoIpRoutes()
    }
}