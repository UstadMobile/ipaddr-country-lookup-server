package com.example.service

import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import java.io.InputStream
import java.net.InetAddress

class GeoIpService {
    private val databaseReader: DatabaseReader

    init {
        val databaseStream: InputStream = this::class.java.classLoader
            .getResourceAsStream(GEOLITE2_DATABASE_FILENAME)
            ?: throw IllegalStateException("GeoLite2 database file not found in resources")

        databaseReader = DatabaseReader.Builder(databaseStream).build()
    }

    fun getCountryCode(ipAddress: String): String? {
        return try {
            val inetAddress = InetAddress.getByName(ipAddress)
            val response = databaseReader.country(inetAddress)
            response.country?.isoCode
        } catch (e: AddressNotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }

    fun close() {
        databaseReader.close()
    }

    companion object {
        private const val GEOLITE2_DATABASE_FILENAME = "GeoLite2-Country.mmdb"
    }
}
