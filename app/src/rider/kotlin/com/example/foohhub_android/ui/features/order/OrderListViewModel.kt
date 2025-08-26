package com.example.foohhub_android.ui.features.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.DeliveryOrder
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(private val foodApi: FoodApi) : ViewModel() {

    private val _state = MutableStateFlow<OrderListState>(OrderListState.Loading)
    val state get() = _state.asStateFlow()

    init {
        getOrders()
    }

    fun getOrders() {
        viewModelScope.launch {
            _state.value = OrderListState.Loading
            val response = safeApiCall { foodApi.getActiveDeliveries() }
            when (response) {
                is ApiResponse.Success -> {
                    if(response.data.data.isEmpty()) {
                        _state.value = OrderListState.Empty
                        return@launch
                    }
                    _state.value = OrderListState.Success(response.data.data)
                }

                is ApiResponse.Error -> {
                    _state.value = OrderListState.Error(response.message)
                }

                else -> {
                    _state.value = OrderListState.Error("Something went wrong")
                }
            }
        }
    }

    sealed class OrderListState {
        object Loading : OrderListState()
        object Empty : OrderListState()
        data class Success(val orders: List<DeliveryOrder>) : OrderListState()
        data class Error(val message: String) : OrderListState()
    }
}