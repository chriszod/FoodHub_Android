package com.example.foodhub_android.di

import android.content.Context
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.remote.FoodHubSession
import com.example.foodhub_android.data.socket.SocketService
import com.example.foodhub_android.data.socket.SocketServiceImpl
import com.example.foodhub_android.location.LocationManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideBaseUrl() = "http://10.0.2.2:8080/" // emulator-safe

    @Provides
    fun provideSession(@ApplicationContext context: Context): FoodHubSession {
        return FoodHubSession(context)
    }

    @Provides
    fun provideLocationService(@ApplicationContext context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideClient(session: FoodHubSession, @ApplicationContext context: Context): OkHttpClient {
        val client = OkHttpClient.Builder()
        client.addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer ${session.getToken()}")
                .addHeader("X-Package-Name", context.packageName)
                .build()
            chain.proceed(request)
        }
        client.addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        return client.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient, baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideFoodApi(retrofit: Retrofit): FoodApi =
        retrofit.create(FoodApi::class.java)

    @Provides
    fun provideSocketService() : SocketService {
        return SocketServiceImpl()
    }

    @Provides
    fun provideLocationManager(
        fusedLocationProviderClient: FusedLocationProviderClient,
        @ApplicationContext context: Context
    ): LocationManager {
        return LocationManager(fusedLocationProviderClient, context)
    }
}