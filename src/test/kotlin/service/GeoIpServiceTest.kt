package service

import com.example.service.GeoIpService
import com.maxmind.geoip2.DatabaseReader
import com.maxmind.geoip2.exception.AddressNotFoundException
import com.maxmind.geoip2.model.CountryResponse
import com.maxmind.geoip2.record.Country
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.net.InetAddress

class GeoIpServiceTest {

    private lateinit var mockDatabaseReader: DatabaseReader
    private lateinit var geoIpService: GeoIpService
    private lateinit var tempDatabaseFile: File

    @BeforeEach
    fun setUp() {
        tempDatabaseFile = File.createTempFile("test_geoip", ".mmdb")
        tempDatabaseFile.writeText("mock database content")

        mockDatabaseReader = mockk()
        mockkConstructor(DatabaseReader.Builder::class)
        every { anyConstructed<DatabaseReader.Builder>().build() } returns mockDatabaseReader
    }

    @AfterEach
    fun tearDown() {
        tempDatabaseFile.delete()
        unmockkAll()
    }

    @Test
    fun `should initialize successfully with valid database path`() {
        val service = GeoIpService(tempDatabaseFile.absolutePath)

        Assertions.assertNotNull(service)
        verify { anyConstructed<DatabaseReader.Builder>().build() }
    }

    @Test
    fun `should throw IllegalStateException when database file does not exist`() {
        val nonExistentPath = "/path/that/does/not/exist/database.mmdb"

        val exception = assertThrows<IllegalStateException> {
            GeoIpService(nonExistentPath)
        }

        Assertions.assertTrue(exception.message!!.contains("GeoLite2 database file not found"))
    }

    @Test
    fun `should return country code for valid public IP`() {
        val testIpAddress = TEST_PUBLIC_IP
        val expectedCountryCode = TEST_COUNTRY_CODE

        val mockCountry = mockk<Country>()
        val mockResponse = mockk<CountryResponse>()

        every { mockResponse.country } returns mockCountry
        every { mockCountry.isoCode } returns expectedCountryCode
        every { mockDatabaseReader.country(any<InetAddress>()) } returns mockResponse

        val service = GeoIpService(tempDatabaseFile.absolutePath)
        val result = service.getCountryForIp(testIpAddress)
        Assertions.assertEquals(com.example.data.CountryResponse(expectedCountryCode), result)
        verify { mockDatabaseReader.country(any<InetAddress>()) }
    }

    @Test
    fun `should return default country code when country data is null`() {
        val testIpAddress = TEST_PUBLIC_IP

        val mockResponse = mockk<CountryResponse>()
        every { mockResponse.country } returns null
        every { mockDatabaseReader.country(any<InetAddress>()) } returns mockResponse

        val service = GeoIpService(tempDatabaseFile.absolutePath)
        val result = service.getCountryForIp(testIpAddress)
        Assertions.assertEquals(com.example.data.CountryResponse(DEFAULT_COUNTRY_CODE), result)
    }


    @Test
    fun `should throw IllegalArgumentException when IP not found in database`() {
        val testIpAddress = TEST_PUBLIC_IP

        every { mockDatabaseReader.country(any<InetAddress>()) } throws AddressNotFoundException("IP not found")

        val service = GeoIpService(tempDatabaseFile.absolutePath)
        val exception = assertThrows<IllegalArgumentException> {
            service.getCountryForIp(testIpAddress)
        }

        Assertions.assertTrue(exception.message!!.contains("IP address not found in GeoIP database"))
    }

    @Test
    fun `should throw RuntimeException for unexpected database errors`() {
        val testIpAddress = TEST_PUBLIC_IP

        every { mockDatabaseReader.country(any<InetAddress>()) } throws RuntimeException("Database error")

        val service = GeoIpService(tempDatabaseFile.absolutePath)

        val exception = assertThrows<RuntimeException> {
            service.getCountryForIp(testIpAddress)
        }

        Assertions.assertTrue(exception.message!!.contains("Internal error resolving IP"))
    }

    @Test
    fun `should close database reader successfully`() {
        val service = GeoIpService(tempDatabaseFile.absolutePath)
        every { mockDatabaseReader.close() } just Runs

        service.close()
        verify { mockDatabaseReader.close() }
    }

    companion object {
        private const val TEST_PUBLIC_IP = "8.8.8.8"
        private const val TEST_COUNTRY_CODE = "US"
        private const val DEFAULT_COUNTRY_CODE = "UNKNOWN"

    }
}