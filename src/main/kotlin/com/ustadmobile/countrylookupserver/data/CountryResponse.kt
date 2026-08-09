package com.ustadmobile.countrylookupserver.data

import kotlinx.serialization.Serializable

/**
 * Response returned by the /json/{host} endpoint.
 *
 * The field names and values match the public ip-api.com JSON API so that this server can be
 * used as a self hosted alternative.
 *
 * Fields which are not applicable are null and are omitted from the serialized JSON e.g. a
 * successful response has no message, a failed response has no countryCode or country.
 *
 * Reference: https://ip-api.com/docs/api:json
 *
 * @param status "success" or "fail"
 * @param countryCode Two-letter ISO 3166-1 alpha-2 country code (e.g., "US"). Null if the lookup failed.
 * @param country Full country name (e.g., "United States"). Null if the lookup failed.
 * @param message Reason for failure (e.g., "private range", "reserved range"). Null if successful.
 * @param query The host as it was requested (e.g., "8.8.8.8")
 */
@Serializable
data class CountryResponse(
    val status: String,
    val countryCode: String? = null,
    val country: String? = null,
    val message: String? = null,
    val query: String? = null
)