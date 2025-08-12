
package com.example

import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*
import kotlinx.serialization.json.*

class ApplicationTest {
    @Test
    fun testCountryEndpoint() = testApplication {
        val response = client.get("/country")
        assertEquals(HttpStatusCode.OK, response.status)

        val responseBody = response.bodyAsText()
        val json = Json.parseToJsonElement(responseBody).jsonObject
        assertTrue(json.containsKey("country"))

        val countryValue = json["country"]?.jsonPrimitive?.content
        assertNotNull(countryValue)
    }
}
