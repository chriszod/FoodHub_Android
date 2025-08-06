package com.example.foodhub_android.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PaymentIntentRequest(
    val addressId: String,
    val paymentMethodId: String? = null
)
