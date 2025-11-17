package com.ustadmobile.countrylookupserver.service

import com.ustadmobile.countrylookupserver.data.CountryResponse
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
            throw IllegalStateException("GeoLite2 database file not found at: $databasePath")
        }
        databaseReader = DatabaseReader.Builder(dbFile).build()
        logger.info("GeoIP service initialized successfully")
    }

    fun getCountryForIp(ipAddress: String): CountryResponse {
        logger.debug("Getting country for IP: $ipAddress")

        try {
            val inetAddress = InetAddress.getByName(ipAddress)

            // Handle local/private IPs like ip-api.com
            if (inetAddress.isLoopbackAddress || inetAddress.isSiteLocalAddress) {
                logger.warn("IP address $ipAddress is local or private")
                return CountryResponse(
                    status = "fail",
                    message = "private range",
                    query = ipAddress
                )
            }

            val response = databaseReader.country(inetAddress)
            val countryCode = response.country?.isoCode

            return if (countryCode != null) {
                logger.debug("Successfully resolved IP $ipAddress to country: $countryCode")
                CountryResponse(
                    status = "success",
                    countryCode = countryCode,  // THIS IS THE FIELD YOUR CLIENT EXPECTS
                    country = response.country?.name,
                    query = ipAddress
                )
            } else {
                logger.debug("IP $ipAddress resolved but no country data available")
                CountryResponse(
                    status = "fail",
                    message = "reserved range",
                    query = ipAddress
                )
            }

        } catch (e: AddressNotFoundException) {
            logger.warn("Address not found in GeoIP database: $ipAddress", e)
            return CountryResponse(
                status = "fail",
                message = "reserved range",
                query = ipAddress
            )
        } catch (e: Exception) {
            logger.error("Unexpected error looking up country for IP: $ipAddress", e)
            return CountryResponse(
                status = "fail",
                message = "query failed",
                query = ipAddress
            )
        }
    }

    fun close() {
        logger.info("Closing GeoIP service")
        databaseReader.close()
    }
}