package com.example.foodhub_android.di

import com.example.foodhub_android.data.repository.CustomerLocationUpdateSocketRepository
import com.example.foodhub_android.data.socket.SocketService
import com.example.foodhub_android.ui.features.orders.LocationUpdateBaseRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object FlavorModule {
    @Provides
    fun provideLocationUpdateSocketRepository(
        socketService: SocketService,
    ): LocationUpdateBaseRepository {
        return CustomerLocationUpdateSocketRepository(socketService)
    }
}