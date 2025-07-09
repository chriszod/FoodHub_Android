package com.example.foodhub_android.data.remote

import android.util.Log
import retrofit2.Response

sealed class ApiResponse<out T> {
    data class Success<out T>(val data: T) : ApiResponse<T>()
    data class Error(val code: Int, val message: String) : ApiResponse<Nothing>() {
        fun formatMsg(): String {
            return "Error $code: $message"
        }
    }
    data class Exception(val exception: kotlin.Exception) : ApiResponse<Nothing>()
}

suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ApiResponse<T> {
    return try {
        val res = apiCall()
        val body = res.body()

        if (res.isSuccessful && body != null) {
            ApiResponse.Success(body)
        } else {
            val errorMsg = res.errorBody()?.string() ?: res.message() ?: "Unknown error"
            Log.d("safeApiCall", "Error body = $errorMsg")
            ApiResponse.Error(res.code(), errorMsg)
        }
    } catch (e: Exception) {
        ApiResponse.Exception(e)
    }
}