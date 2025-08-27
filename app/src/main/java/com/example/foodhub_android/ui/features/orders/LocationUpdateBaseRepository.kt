package com.example.foodhub_android.ui.features.orders

import com.example.foodhub_android.data.socket.SocketService

abstract class LocationUpdateBaseRepository (val socketService: SocketService)
{
    open val messages = socketService.messages
    abstract fun connect(orderID: String, riderID: String)
    abstract fun disconnect()
}