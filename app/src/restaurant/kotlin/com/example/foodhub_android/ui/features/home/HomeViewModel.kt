package com.example.foodhub_android.ui.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.Restaurant
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.FoodHubSession
import com.example.foodhub_android.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(val foodApi: FoodApi, val session: FoodHubSession) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getRestaurantProfile()
    }

    fun getRestaurantProfile() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val response = safeApiCall { foodApi.getRestaurantProfile() }
            when (response) {
                is ApiResponse.Success -> {
                    _uiState.value = HomeUiState.Success(response.data)
                    session.storeRestaurantId(response.data.id)
                }
                is ApiResponse.Error -> {
                    _uiState.value = HomeUiState.Error(response.message)
                }
                else -> {}
            }
        }
    }

    fun retry() {
        getRestaurantProfile()
    }
    sealed class HomeUiState {
        object Idle : HomeUiState()
        object Loading : HomeUiState()
        data class Success(val data: Restaurant) : HomeUiState()
        data class Error(val message: String? = null) : HomeUiState()
    }
}