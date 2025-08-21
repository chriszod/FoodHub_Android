package com.example.foodhub_android

import android.app.Application
import com.example.foodhub_android.ui.features.notification.FoodHubNotificationManager
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class FoodHubApp: Application() {
    @Inject
    lateinit var foodHubNotificationManager: FoodHubNotificationManager
    override fun onCreate() {
        super.onCreate()
        foodHubNotificationManager.createChannels()
        foodHubNotificationManager.getAndStoreToken()
    }
}