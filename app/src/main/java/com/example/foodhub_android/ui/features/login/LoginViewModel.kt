package com.example.foodhub_android.ui.features.login

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.auth.GoogleAuthUiProvider
import com.example.foodhub_android.data.models.LoginRequest
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(val foodApi: FoodApi)
    : BaseAuthViewModel<LoginViewModel.LoginNavigationEvent>() {
    val googleAuthUiProvider = GoogleAuthUiProvider()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
    }

    fun onLoginClick() {
        val request = LoginRequest(email.value, password.value)
        handleApiCall(
            call = { foodApi.login(request) },
            success = { navigate(LoginNavigationEvent.NavigateToHome) },
            onError = {setUiState(BaseUiState.Error(it))}
        )
    }

    fun onSignupCLick() {
        viewModelScope.launch {
            navigate(LoginNavigationEvent.NavigateToSignup)
        }
    }

    fun onGoogleClick(context: Context) {
        viewModelScope.launch {
            val response = googleAuthUiProvider.signIn(
                activityContext = context,
                credentialManager = CredentialManager.create(context)
            )
            val request = OAuthRequest(response.token, "google")
            handleApiCall(
                call = { foodApi.oAuth(request) },
                success = { navigate(LoginNavigationEvent.NavigateToHome) },
                onError = {setUiState(BaseUiState.Error(it))}
            )
        }
    }

    fun onDialogDismissed() {
        _uiState.value = BaseUiState.Idle
    }

    sealed class LoginNavigationEvent {
        object NavigateToSignup : LoginNavigationEvent()
        object NavigateToHome : LoginNavigationEvent()
    }
}
