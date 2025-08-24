package com.example.foodhub_android.ui.features.order

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.Order
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import com.example.foodhub_android.utils.OrdersUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderListViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    private val _uiState = MutableStateFlow<OrderScreenState>(OrderScreenState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getOrderTypes(): List<String> {
        val types = OrdersUtils.OrderStatus.entries.map { it.name }
        return types
    }

    fun getOrdersByType(status: String) {
        viewModelScope.launch {
            _uiState.value = OrderScreenState.Loading
            val response = safeApiCall { foodApi.getRestaurantOrders(status) }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = OrderScreenState.Success(response.data.orders)
                }

                else -> {
                    _uiState.value = OrderScreenState.Failed
                }
            }
        }
    }

    sealed class OrderScreenState {
        object Loading : OrderScreenState()
        object Failed : OrderScreenState()
        data class Success(val data: List<Order>) : OrderScreenState()
    }
}
