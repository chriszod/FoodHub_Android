package com.example.foohhub_android.di

import com.example.foodhub_android.data.socket.SocketService
import com.example.foodhub_android.location.LocationManager
import com.example.foodhub_android.ui.features.orders.LocationUpdateBaseRepository
import com.example.foohhub_android.ui.features.order.repository.LocationUpdateSocketRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule{
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
        locationManager: LocationManager
    ): LocationUpdateBaseRepository {
        return LocationUpdateSocketRepository(socketService, locationManager)
    }
}