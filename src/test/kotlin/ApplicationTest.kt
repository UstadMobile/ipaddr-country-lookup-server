package com.ustadmobile.countrylookupserver

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlinx.serialization.json.*

class ApplicationTest {
    @Test
    fun testCountryEndpointWithValidPublicIp() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", GOOGLE_DNS_IP)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertTrue(json.containsKey("country"))

        val countryValue = json["country"]?.jsonPrimitive?.content
        assertNotNull(countryValue)
        assertEquals(EXPECTED_US_COUNTRY_CODE, countryValue)
    }

    @Test
    fun testCountryEndpointWithCloudflareIp() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", CLOUDFLARE_DNS_IP)
        }
        assertEquals(HttpStatusCode.NotFound, response.status)

        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("Could not determine country for IP"))
    }

    @Test
    fun testCountryEndpointWithLocalIpAddress() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", LOCALHOST_IP)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("Local or private IP addresses are not allowed"))
    }

    @Test
    fun testCountryEndpointWithPrivateIpAddress() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", PRIVATE_IP_ADDRESS)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("Local or private IP addresses are not allowed"))
    }

    @Test
    fun testCountryEndpointWithInvalidIpAddress() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", INVALID_IP_ADDRESS)
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)

        val responseBody = response.bodyAsText()
        assertTrue(responseBody.contains("Invalid IP address"))
    }

    @Test
    fun testCountryEndpointWithMultipleForwardedIps() = testApplication {
        val multipleIps = "$GOOGLE_DNS_IP, $PRIVATE_IP_ADDRESS, $LOCALHOST_IP"
        val response = client.get("/country") {
            header("X-Forwarded-For", multipleIps)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertTrue(json.containsKey("country"))

        val countryValue = json["country"]?.jsonPrimitive?.content
        assertNotNull(countryValue)
        assertEquals(EXPECTED_US_COUNTRY_CODE, countryValue)
    }
    @Test
    fun testCountryEndpointWithNonsenseIp() = testApplication {
        val response = client.get("/country") {
            header("X-Forwarded-For", "not.an.ip")
        }
        assertEquals(HttpStatusCode.InternalServerError, response.status)
    }

    @Test
    fun testCountryEndpointWithXRealIpHeader() = testApplication {
        val response = client.get("/country") {
            header("X-Real-IP", GOOGLE_DNS_IP)
        }
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertTrue(json.containsKey("country"))

        val countryValue = json["country"]?.jsonPrimitive?.content
        assertNotNull(countryValue)
        assertEquals(EXPECTED_US_COUNTRY_CODE, countryValue)
    }

    @Test
    fun testCountryEndpointWithoutHeaders() = testApplication {
        val response = client.get("/country")

        assertTrue(
            response.status == HttpStatusCode.OK ||
                    response.status == HttpStatusCode.InternalServerError
        )

        val responseBody = response.bodyAsText()
        if (response.status == HttpStatusCode.OK) {
            val json = Json.parseToJsonElement(responseBody).jsonObject
            assertTrue(json.containsKey("country"))
            val countryValue = json["country"]?.jsonPrimitive?.content
            assertNotNull(countryValue)
        }
    }

    companion object {
        const val GOOGLE_DNS_IP = "8.8.8.8"
        const val CLOUDFLARE_DNS_IP = "1.1.1.1"
        const val LOCALHOST_IP = "127.0.0.1"
        const val PRIVATE_IP_ADDRESS = "192.168.1.1"
        const val INVALID_IP_ADDRESS = "999.999.999.999"
        const val EXPECTED_US_COUNTRY_CODE = "US"
    }
}