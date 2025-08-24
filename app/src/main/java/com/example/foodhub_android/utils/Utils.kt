package com.example.foodhub_android.utils

import java.text.NumberFormat
import java.util.Currency

object StringUtils {
    fun formatCurrency(value: Double): String {
        val currencyFormatter = NumberFormat.getCurrencyInstance()
        currencyFormatter.currency = Currency.getInstance("USD")
        return currencyFormatter.format(value)
    }
}

object OrdersUtils {

    enum class OrderStatus {
        PENDING_ACCEPTANCE, // Initial state when order is placed
        ACCEPTED,          // Restaurant accepted the order
        PREPARING,         // Food is being prepared
        READY,
        ASSIGNED,         // Rider assigned
        OUT_FOR_DELIVERY, // Rider picked up
        DELIVERED,        // Order completed
        REJECTED,         // Restaurant rejected the order
        CANCELLED         // Customer cancelled// Ready for delivery/pickup
    }
}