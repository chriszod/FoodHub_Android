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