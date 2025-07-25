package com.example.foodhub_android.data.models

data class UpdateCartItemRequest(
    val cartItemId: String,
    val quantity: Int
)
