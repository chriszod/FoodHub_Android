package com.example.foodhub_android.data.models

data class Deliveries(
    val createdAt: String,
    val customerAddress: String,
    val estimatedDistance: Double,
    val estimatedEarning: Double,
    val orderAmount: Double,
    val orderId: String,
    val restaurantAddress: String,
    val restaurantName: String
)