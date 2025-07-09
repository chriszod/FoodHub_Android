package com.example.foodhub_android.data

import com.example.foodhub_android.data.models.SignUpRequest
import com.example.foodhub_android.data.models.AuthResponse
import com.example.foodhub_android.data.models.CategoriesResponse
import com.example.foodhub_android.data.models.LoginRequest
import com.example.foodhub_android.data.models.OAuthRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface FoodApi {
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>

    @GET("/categories")
    suspend fun getCategories(): Response<CategoriesResponse>
}