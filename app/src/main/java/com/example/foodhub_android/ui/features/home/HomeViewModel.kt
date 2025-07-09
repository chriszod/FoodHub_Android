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
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor (val foodApi: FoodApi) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Idle)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<HomeNavigationEvent>()
    val navigationEvent: SharedFlow<HomeNavigationEvent> = _navigationEvent.asSharedFlow()

    var categories = emptyList<Category>()

    init {
        getCategories()
        getPopularRestaurants()
    }

    private fun getPopularRestaurants() {

    }

    private fun getCategories() {
        viewModelScope.launch {
            val response = safeApiCall { foodApi.getCategories() }
            when (response) {
                is ApiResponse.Success -> {
                    categories = response.data.data
                    _uiState.value = HomeUiState.Success(response.data)
                }
                is ApiResponse.Error -> {
                    _uiState.value = HomeUiState.Error(response.message)
                }
                is ApiResponse.Exception -> {
                    _uiState.value = HomeUiState.Error(response.exception.localizedMessage ?: "Unknown error")
                }
            }
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
    }
}