package com.example.usecase

import com.example.data.CountryResponse
import com.example.service.GeoIpService

class GetCountryForIpUseCase(
    private val geoIpService: GeoIpService
) {
    fun execute(ipAddress: String): CountryResponse {
        val countryCode = geoIpService.getCountryCode(ipAddress)
        return CountryResponse(
            country = countryCode ?: DEFAULT_COUNTRY_CODE
        )
    }

    companion object {
        private const val DEFAULT_COUNTRY_CODE = "UNKNOWN"
    }
}