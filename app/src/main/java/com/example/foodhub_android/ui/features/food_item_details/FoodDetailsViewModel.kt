package com.example.foodhub_android.ui.features.food_item_details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.AddToCartRequest
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
class FoodDetailsViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {

    private val _uiState = MutableStateFlow<FoodDetailsUiState>(FoodDetailsUiState.Idle)
    val uiState: StateFlow<FoodDetailsUiState> = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<FoodDetailsEvent>()
    val event: SharedFlow<FoodDetailsEvent> = _event.asSharedFlow()

    private val _quantity = MutableStateFlow(1)
    val quantity: StateFlow<Int> = _quantity.asStateFlow()

    fun addToCart(restaurantId: String, foodItemId: String) {
        viewModelScope.launch {
            _uiState.value = FoodDetailsUiState.Loading
            val response = safeApiCall {
                foodApi.addToCart(
                    AddToCartRequest(
                        restaurantId,
                        foodItemId,
                        _quantity.value
                    )
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = FoodDetailsUiState.Success
                    _event.emit(FoodDetailsEvent.OnAddToCartSuccess)
                }
                is ApiResponse.Error -> {
                    _uiState.value = FoodDetailsUiState.Error(response.message)
                }
                else -> {
                    _uiState.value = FoodDetailsUiState.Error("Unknown exception")
                }
            }
        }
    }

    fun increaseQuantity() {
        _quantity.value += 1
    }

    fun decreaseQuantity() {
        if (_quantity.value > 1) {
            _quantity.value -= 1
        }
    }

    fun onDialogDismissed() {
        _uiState.value = FoodDetailsUiState.Idle
    }

    sealed class FoodDetailsUiState {
        object Idle : FoodDetailsUiState()
        object Loading : FoodDetailsUiState()
        object Success : FoodDetailsUiState()
        data class Error(val message: String) : FoodDetailsUiState()
    }

    sealed class FoodDetailsEvent {
        object OnAddToCartSuccess : FoodDetailsEvent()
    }
}