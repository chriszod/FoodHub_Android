package com.example.foodhub_android.data

import com.example.foodhub_android.data.models.AddToCartRequest
import com.example.foodhub_android.data.models.AddToCartResponse
import com.example.foodhub_android.data.models.SignUpRequest
import com.example.foodhub_android.data.models.AuthResponse
import com.example.foodhub_android.data.models.CategoriesResponse
import com.example.foodhub_android.data.models.LoginRequest
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.data.models.RestaurantsResponse
import com.example.foodhub_android.data.models.FoodItemResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>

    @GET("/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<RestaurantsResponse>

    @GET("/restaurants/{restaurantId}/menu")
    suspend fun getFoodItemForRestaurant(
        @Path("restaurantId") restaurantId: String
    ): Response<FoodItemResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>
}