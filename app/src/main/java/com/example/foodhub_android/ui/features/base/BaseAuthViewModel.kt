package com.example.foodhub_android.ui.features.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel.BaseUiState.Idle
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel.BaseUiState.Loading
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel.BaseUiState.Success
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

abstract class BaseAuthViewModel<NavEventType : Any> : ViewModel() {
    interface BaseUiState {
        object Idle : BaseUiState
        object Loading : BaseUiState
        object Success : BaseUiState
        data class Error(val message: String? = null) : BaseUiState
    }

    protected val _uiState = MutableStateFlow<BaseUiState>(Idle)
    val uiState: StateFlow<BaseUiState> = _uiState.asStateFlow()

    protected val _navigationEvent = MutableSharedFlow<NavEventType>()
    val navigationEvent: SharedFlow<NavEventType> = _navigationEvent.asSharedFlow()

    protected fun <T> handleApiCall(
        call: suspend () -> Response<T>,
        success: suspend (T) -> Unit,
        successState: BaseUiState = Success,
        onError: (String) -> Unit = { setUiState(BaseUiState.Error(it)) }
    ) {
        viewModelScope.launch {
            setUiState(Loading)

            val res = safeApiCall(call)
            when (res) {
                is ApiResponse.Success -> {
                    success(res.data)
                    setUiState(successState)
                }
                is ApiResponse.Error -> {
                    onError(res.formatMsg())
                }
                is ApiResponse.Exception -> {
                    onError(res.exception.localizedMessage ?: "Unexpected error")
                }
            }
        }
    }

    protected suspend fun navigate(event: NavEventType) {
        _navigationEvent.emit(event)
    }

    protected fun setUiState(state: BaseUiState) {
        _uiState.value = state
    }
}