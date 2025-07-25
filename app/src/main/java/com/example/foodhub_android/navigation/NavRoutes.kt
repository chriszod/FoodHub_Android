package com.example.foodhub_android.navigation

import com.example.foodhub_android.data.models.FoodItem
import kotlinx.serialization.Serializable

@Serializable
object Auth

@Serializable
object Login

@Serializable
object SignUp

@Serializable
object Home

@Serializable
data class RestaurantDetails(
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String
)

@Serializable
data class FoodItemDetails(
    val foodItem: FoodItem
)

@Serializable
object Cart