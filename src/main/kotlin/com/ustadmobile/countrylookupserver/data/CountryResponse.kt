package com.ustadmobile.countrylookupserver.data

import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(
    val status: String,
    val countryCode: String? = null,
    val country: String? = null,
    val message: String? = null,
    val query: String? = null
)