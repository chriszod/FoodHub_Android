package com.example.foodhub_android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class NextStop(
    val address: String,
    val latitude: Double,
    val longitude: Double
)