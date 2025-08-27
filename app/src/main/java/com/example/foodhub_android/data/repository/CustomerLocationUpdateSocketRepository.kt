package com.example.foodhub_android.data.repository

import com.example.foodhub_android.data.socket.SocketService
import com.example.foodhub_android.ui.features.orders.LocationUpdateBaseRepository
import javax.inject.Inject

class CustomerLocationUpdateSocketRepository @Inject constructor(socketService: SocketService) :
    LocationUpdateBaseRepository(socketService) {

    override fun connect(orderID: String, riderID: String) {
        try {
            socketService.connect(
                orderID, riderID, null, null
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
        }
    }

    override fun disconnect() {
        socketService.disconnect()
    }
}