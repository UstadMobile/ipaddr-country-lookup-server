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
        val response = client.get("/json/${GOOGLE_DNS_IP}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertEquals("success", json["status"]?.jsonPrimitive?.content)
        assertEquals(EXPECTED_US_COUNTRY_CODE, json["countryCode"]?.jsonPrimitive?.content)
        assertEquals(GOOGLE_DNS_IP, json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithCloudflareIp() = testApplication {
        val response = client.get("/json/${CLOUDFLARE_DNS_IP}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertEquals("success", json["status"]?.jsonPrimitive?.content)
        assertNotNull(json["countryCode"]?.jsonPrimitive?.content)
        assertEquals(CLOUDFLARE_DNS_IP, json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithLocalIpAddress() = testApplication {
        val response = client.get("/json/${LOCALHOST_IP}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertEquals("fail", json["status"]?.jsonPrimitive?.content)
        assertEquals("private range", json["message"]?.jsonPrimitive?.content)
        assertEquals(LOCALHOST_IP, json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithPrivateIpAddress() = testApplication {
        val response = client.get("/json/${PRIVATE_IP_ADDRESS}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertEquals("fail", json["status"]?.jsonPrimitive?.content)
        assertEquals("private range", json["message"]?.jsonPrimitive?.content)
        assertEquals(PRIVATE_IP_ADDRESS, json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithInvalidIpAddress() = testApplication {
        val response = client.get("/json/${INVALID_IP_ADDRESS}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertEquals("fail", json["status"]?.jsonPrimitive?.content)
        assertTrue(json["message"]?.jsonPrimitive?.content?.isNotEmpty() == true)
        assertEquals(INVALID_IP_ADDRESS, json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithDomainName() = testApplication {
        val response = client.get("/json/google.com")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertEquals("success", json["status"]?.jsonPrimitive?.content)
        assertNotNull(json["countryCode"]?.jsonPrimitive?.content)
        assertEquals("google.com", json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithEmptyHost() = testApplication {
        val response = client.get("/json/")
        assertEquals(HttpStatusCode.BadRequest, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertEquals("fail", json["status"]?.jsonPrimitive?.content)
        assertEquals("Invalid query", json["message"]?.jsonPrimitive?.content)
    }

    @Test
    fun testCountryEndpointWithoutPathParameter() = testApplication {
        val response = client.get("/json")
        assertEquals(HttpStatusCode.NotFound, response.status)
    }

    @Test
    fun testCountryEndpointWithNonsenseInput() = testApplication {
        val response = client.get("/json/not.an.ip.or.domain")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertEquals("fail", json["status"]?.jsonPrimitive?.content)
        assertTrue(json["message"]?.jsonPrimitive?.content?.isNotEmpty() == true)
        assertEquals("not.an.ip.or.domain", json["query"]?.jsonPrimitive?.content)
    }

    @Test
    fun testResponseFormatMatchesIpApiCom() = testApplication {
        val response = client.get("/json/${GOOGLE_DNS_IP}")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject

        assertTrue(json.containsKey("status"))
        assertTrue(json.containsKey("countryCode"))
        assertTrue(json.containsKey("country"))
        assertTrue(json.containsKey("query"))
        assertTrue(json.containsKey("message"))

        val status = json["status"]?.jsonPrimitive?.content
        assertTrue(status == "success" || status == "fail")

        if (status == "success") {
            assertNotNull(json["countryCode"]?.jsonPrimitive?.content)
            assertTrue(json["countryCode"]?.jsonPrimitive?.content?.length == 2)
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