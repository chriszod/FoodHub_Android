package com.example.foodhub_android.ui.features.home

import androidx.lifecycle.ViewModel
import com.example.foodhub_android.data.FoodApi
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.models.Category
import com.example.foodhub_android.data.models.Restaurant
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent: SharedFlow<HomeNavigationEvent> = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()
    var restaurants = emptyList<Restaurant>()

    init {
        viewModelScope.launch {
            categories = getCategories()
            restaurants = getPopularRestaurants()

            if (categories.isEmpty() && restaurants.isEmpty()) {
                _uiState.value = HomeUiState.Error("No data found")
            } else {
                _uiState.value = HomeUiState.Success(Unit)
            }
        }
    }

    private suspend fun getPopularRestaurants(): List<Restaurant> {
        var list = emptyList<Restaurant>()
        val response = safeApiCall { foodApi.getRestaurants(40.7128, -74.0060) }
        when (response) {
            is ApiResponse.Success -> {
                list = response.data.data
                _uiState.value = HomeUiState.Success(response.data)
            }

            else -> {}
        }
        return list
    }

    private suspend fun getCategories(): List<Category> {
        var list = emptyList<Category>()
        val response = safeApiCall { foodApi.getCategories() }
        when (response) {
            is ApiResponse.Success -> {
                list = response.data.data
                _uiState.value = HomeUiState.Success(response.data)
            }

            else -> {}
        }
        return list
    }

    fun onRestaurantClick(restaurant: Restaurant) {
        viewModelScope.launch {
            _navigationEvent.emit(
                HomeNavigationEvent.NavigateToRestaurantDetails(
                    restaurant.name,
                    restaurant.imageUrl,
                    restaurant.id
                )
            )
        }
    }

    sealed class HomeUiState {
        object Idle : HomeUiState()
        object Loading : HomeUiState()
        data class Success(val data: Any) : HomeUiState()
        data class Error(val message: String) : HomeUiState()
    }

    sealed class HomeNavigationEvent {
        object NavigateToLogin : HomeNavigationEvent()
        data class NavigateToRestaurantDetails(
            val name: String,
            val imageUrl: String,
            val restaurantId: String
        ) : HomeNavigationEvent()
    }
}