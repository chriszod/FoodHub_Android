package com.example.foodhub_android.ui.features.restaurant_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.data.models.Restaurant
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RestaurantViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel(){
    private val _uiState = MutableStateFlow<RestaurantUiState>(RestaurantUiState.Idle)
    val uiState: StateFlow<RestaurantUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<RestaurantNavigationEvent>()
    val navigationEvent: SharedFlow<RestaurantNavigationEvent> = _navigationEvent.asSharedFlow()

    fun getFoodItems(id: String) {
        viewModelScope.launch {
            _uiState.value = RestaurantUiState.Loading
            val response = safeApiCall { foodApi.getFoodItemForRestaurant(id) }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = RestaurantUiState.Success(response.data.foodItems)
                }
                is ApiResponse.Error -> {
                    _uiState.value = RestaurantUiState.Error(response.message)
                }
                else -> {}
            }
        }
    }

    sealed class RestaurantUiState {
        object Idle : RestaurantUiState()
        object Loading : RestaurantUiState()
        data class Success(val foodItems: List<FoodItem>) : RestaurantUiState()
        data class Error(val message: String) : RestaurantUiState()
    }

    sealed class RestaurantNavigationEvent {
        object GoBack : RestaurantNavigationEvent()
        data class NavigateToProductItem(val productId: Int) : RestaurantNavigationEvent()
    }
}