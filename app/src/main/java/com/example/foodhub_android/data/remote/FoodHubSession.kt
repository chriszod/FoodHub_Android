package com.example.foodhub_android.data.remote

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import androidx.core.content.edit

class FoodHubSession(val context: Context) {
    val sharedPres: SharedPreferences =
        context.getSharedPreferences("foodhub", Context.MODE_PRIVATE)


    fun storeToken(token: String) {
        sharedPres.edit { putString("token", token) }
    }

    fun getToken(): String? {
        sharedPres.getString("token", null)?.let {
            return it
        }
        return null
    }

    fun storeRestaurantId(restaurantId: String) {
        sharedPres.edit { putString("restaurantId", restaurantId) }
    }

    fun getRestaurantId(): String? {
        sharedPres.getString("restaurantId", null)?.let {
            return it
        }
        return null
    }
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val session: FoodHubSession
) : ViewModel() {
    fun hasToken(): Boolean = session.getToken() != null
}