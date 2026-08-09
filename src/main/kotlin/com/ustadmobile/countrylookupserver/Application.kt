package com.ustadmobile.countrylookupserver

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.ustadmobile.countrylookupserver.plugins.configureHTTP
import com.ustadmobile.countrylookupserver.plugins.configureRouting
import com.ustadmobile.countrylookupserver.plugins.configureSerialization
import com.ustadmobile.countrylookupserver.service.GeoIpService
import java.io.File

private const val SERVER_PORT = 8080

private const val SERVER_HOST = "0.0.0.0"

private const val DEFAULT_MAXMIND_FILENAME = "GeoLite2-Country.mmdb"

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = SERVER_HOST, module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureHTTP()
    configureSerialization()

    val geoDbPath = System.getenv("GEO_DATABASE_PATH")
        ?: environment.config.propertyOrNull("geo.database.path")?.getString()
        ?: File(DEFAULT_MAXMIND_FILENAME).takeIf { it.exists() }?.absolutePath
        ?: throw IllegalStateException("GeoIP database path is not configured. Please set GEO_DATABASE_PATH environment variable or configure geo.database.path in application.conf")

    val geoIpService = GeoIpService(geoDbPath)

    environment.monitor.subscribe(ApplicationStopped) {
        geoIpService.close()
    }

    configureRouting(geoIpService)
}
