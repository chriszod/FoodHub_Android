package com.example.foodhub_android.data.models

data class SignUpRequest(
    val name: String,
    val email: String,
    val password: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class OAuthRequest(
    val token: String,
    val provider: String
)