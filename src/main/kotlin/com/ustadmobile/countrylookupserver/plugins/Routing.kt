package com.ustadmobile.countrylookupserver.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import com.ustadmobile.countrylookupserver.routes.configureGeoIpRoutes
import com.ustadmobile.countrylookupserver.service.GeoIpService

fun Application.configureRouting(geoIpService: GeoIpService) {
    routing {
        configureGeoIpRoutes(geoIpService)
    }
}