package com.example.foodhub_android.data.models

data class Item(
    val addedAt: String,
    val id: String,
    val menuItemId: MenuItemId,
    val quantity: Int,
    val restaurantId: String,
    val userId: String
)