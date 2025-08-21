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

        databaseReader = if (databasePath.startsWith(CLASSPATH_PREFIX)) {
            // Load from classpath/resources
            val resourcePath = databasePath.removePrefix(CLASSPATH_PREFIX)
            val inputStream = this::class.java.classLoader.getResourceAsStream(resourcePath)
                ?: throw IllegalStateException("GeoLite2 database file not found in resources: $resourcePath. Please download and place the file as described in README.md")

            logger.info("Loading GeoIP database from classpath: $resourcePath")
            DatabaseReader.Builder(inputStream).build()
        } else {
            // Load from file system
            val dbFile = File(databasePath)
            if (!dbFile.exists()) {
                throw IllegalStateException("GeoLite2 database file not found at: $databasePath. Please download and configure the database path as described in README.md")
            }

            logger.info("Loading GeoIP database from file system: $databasePath")
            DatabaseReader.Builder(dbFile).build()
        }

        logger.info("GeoIP service initialized successfully")
    }

    fun getCountryCode(ipAddress: String): String? {
        return try {
            val inetAddress = InetAddress.getByName(ipAddress)
            val response = databaseReader.country(inetAddress)
            val countryCode = response.country?.isoCode

            if (countryCode != null) {
                logger.debug("Successfully resolved IP $ipAddress to country: $countryCode")
            } else {
                logger.debug("IP $ipAddress resolved but no country data available")
            }

            countryCode
        } catch (e: AddressNotFoundException) {
            logger.debug("Address not found in GeoIP database: $ipAddress")
            null
        } catch (e: IllegalArgumentException) {
            logger.warn("Invalid IP address format: $ipAddress", e)
            throw IllegalArgumentException("Invalid IP address format: $ipAddress", e)
        } catch (e: Exception) {
            logger.error("Unexpected error looking up country for IP: $ipAddress", e)
            throw e
        }
    }

    fun getCountryForIp(ipAddress: String): CountryResponse {
        logger.debug("Getting country for IP: $ipAddress")

        val countryCode = getCountryCode(ipAddress)
        val finalCountryCode = countryCode ?: DEFAULT_COUNTRY_CODE

        logger.debug("Returning country code: $finalCountryCode for IP: $ipAddress")

        return CountryResponse(
            country = finalCountryCode
        )
    }

    fun close() {
        logger.info("Closing GeoIP service")
        databaseReader.close()
        logger.info("GeoIP service closed")
    }

    companion object {
        private const val CLASSPATH_PREFIX = "classpath:"
        private const val DEFAULT_COUNTRY_CODE = "UNKNOWN"
    }
}