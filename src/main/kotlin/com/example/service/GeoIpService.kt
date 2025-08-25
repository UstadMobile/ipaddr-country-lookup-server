package com.example.service

import com.example.data.CountryResponse
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import org.slf4j.LoggerFactory
import java.io.File
import java.net.InetAddress

class GeoIpService(databasePath: String) {

    private val databaseReader: DatabaseReader
    private val logger = LoggerFactory.getLogger(GeoIpService::class.java)

    init {
        logger.info("Initializing GeoIP service with database path: $databasePath")

        val dbFile = File(databasePath)
        if (!dbFile.exists()) {
            throw IllegalStateException("GeoLite2 database file not found at: $databasePath. Please download and configure the database path as described in README.md")
        }

        logger.info("Loading GeoIP database from file system: $databasePath")
        databaseReader = DatabaseReader.Builder(dbFile).build()

        logger.info("GeoIP service initialized successfully")
    }

    fun getCountryForIp(ipAddress: String): CountryResponse {
        logger.debug("Getting country for IP: $ipAddress")

        return try {
            val inetAddress = InetAddress.getByName(ipAddress)

            if (inetAddress.isLoopbackAddress || inetAddress.isSiteLocalAddress) {
                logger.warn("IP address $ipAddress is local or private — rejecting")
                throw IllegalArgumentException("Local or private IP addresses are not allowed: $ipAddress")
            }
            val response = databaseReader.country(inetAddress)
            val countryCode = response.country?.isoCode

            val finalCountryCode = countryCode ?: DEFAULT_COUNTRY_CODE

            if (countryCode != null) {
                logger.debug("Successfully resolved IP $ipAddress to country: $countryCode")
            } else {
                logger.debug("IP $ipAddress resolved but no country data available")
            }

            logger.debug("Returning country code: $finalCountryCode for IP: $ipAddress")

            CountryResponse(country = finalCountryCode)

        } catch (e: AddressNotFoundException) {
            logger.warn("Address not found in GeoIP database: $ipAddress", e)
            throw IllegalArgumentException("IP address not found in GeoIP database: $ipAddress", e)

        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid IP address format: $ipAddress", e)
            throw IllegalArgumentException("Invalid IP address format: $ipAddress", e)

        } catch (e: Exception) {
            logger.error("Unexpected error looking up country for IP: $ipAddress", e)
            throw RuntimeException("Internal error resolving IP: $ipAddress", e)
        }
    }

    fun close() {
        logger.info("Closing GeoIP service")
        databaseReader.close()
        logger.info("GeoIP service closed")
    }

    companion object {
        private const val DEFAULT_COUNTRY_CODE = "UNKNOWN"
    }
}