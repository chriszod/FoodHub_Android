package com.example.foodhub_android.ui.features.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.auth.GoogleAuthUiProvider
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(val foodApi: FoodApi)
    : BaseAuthViewModel<AuthViewModel.AuthNavigationEvent>() {
    val googleAuthUiProvider = GoogleAuthUiProvider()

    fun onGoogleClick(context: Context) {
        viewModelScope.launch {
            val response = googleAuthUiProvider.signIn(
                activityContext = context,
                credentialManager = CredentialManager.create(context)
            )
            val request = OAuthRequest(response.token, "google")
            handleApiCall(
                call = { foodApi.oAuth(request) },
                success = { navigate(AuthNavigationEvent.NavigateToHome) },
                onError = {setUiState(BaseUiState.Error(it))}
            )
        }
    }

    fun onSignupCLick() {
        viewModelScope.launch {
            navigate(AuthNavigationEvent.NavigateToSignup)
        }
    }

    fun onLoginCLick() {
        viewModelScope.launch {
            navigate(AuthNavigationEvent.NavigateToLogin)
        }
    }

    fun onDialogDismissed() {
        _uiState.value = BaseUiState.Idle
    }

    sealed class AuthNavigationEvent {
        object NavigateToSignup : AuthNavigationEvent()
        object NavigateToLogin : AuthNavigationEvent()
        object NavigateToHome : AuthNavigationEvent()
    }
}