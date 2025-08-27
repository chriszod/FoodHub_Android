package com.example.foodhub_android.ui.features.order

import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.R
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.Order
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import com.example.foodhub_android.ui.features.orders.LocationUpdateBaseRepository
import com.example.foodhub_android.ui.features.orders.OrderDetailsBaseViewModel
import com.example.foodhub_android.utils.OrdersUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailsViewModel @Inject constructor(
    private val foodApi: FoodApi, repository: LocationUpdateBaseRepository
) : OrderDetailsBaseViewModel(repository) {

    private val _state = MutableStateFlow<OrderDetailsState>(OrderDetailsState.Loading)
    val state get() = _state.asStateFlow()

    private val _event = MutableSharedFlow<OrderDetailsEvent>()
    val event get() = _event.asSharedFlow()


    fun getOrderDetails(orderId: String) {
        viewModelScope.launch {
            _state.value = OrderDetailsState.Loading
            val result = safeApiCall { foodApi.getOrderDetails(orderId) }
            when (result) {
                is ApiResponse.Success -> {
                    _state.value = OrderDetailsState.OrderDetails(result.data)

                    if (result.data.status == OrdersUtils.OrderStatus.OUT_FOR_DELIVERY.name) {
                        result.data.riderId?.let {
                            connectSocket(orderId, it)
                        }
                    } else {
                        if (result.data.status == OrdersUtils.OrderStatus.DELIVERED.name
                            || result.data.status == OrdersUtils.OrderStatus.CANCELLED.name
                            || result.data.status == OrdersUtils.OrderStatus.REJECTED.name) {
                            disconnectSocket()
                        }
                    }
                }

                is ApiResponse.Error -> {
                    _state.value = OrderDetailsState.Error(result.message)
                }

                is ApiResponse.Exception -> {
                    _state.value =
                        OrderDetailsState.Error(result.exception.message ?: "An error occurred")
                }
            }
        }
    }

    fun navigateBack() {
        viewModelScope.launch {
            _event.emit(OrderDetailsEvent.NavigateBack)
        }
    }

    fun getImage(order: Order): Int {
        return when (order.status) {
            "Delivered" -> R.drawable.ic_delivered
            "Preparing" -> R.drawable.ic_delivered
            "On the way" -> R.drawable.ic_delivered
            else -> R.drawable.ic_delivered
        }
    }

    sealed class OrderDetailsEvent {
        object NavigateBack : OrderDetailsEvent()
    }

    sealed class OrderDetailsState {
        object Loading : OrderDetailsState()
        data class OrderDetails(val order: Order) : OrderDetailsState()
        data class Error(val message: String) : OrderDetailsState()
    }
}