package com.example.data

import kotlinx.serialization.Serializable

@Serializable
data class CountryResponse(
    val country: String
)