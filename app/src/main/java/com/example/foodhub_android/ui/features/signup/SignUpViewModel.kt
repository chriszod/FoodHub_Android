package com.example.foodhub_android.ui.features.signup

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.auth.GoogleAuthUiProvider
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.data.models.SignUpRequest
import com.example.foodhub_android.data.remote.FoodHubSession
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(val foodApi: FoodApi, val session: FoodHubSession) :
    BaseAuthViewModel<SignUpViewModel.SignUpNavigationEvent>() {
    val googleAuthUiProvider = GoogleAuthUiProvider()

    private val _fullName = MutableStateFlow("")
    val fullName: StateFlow<String> = _fullName.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onFullNameChange(fullName: String) {
        _fullName.value = fullName
    }

    fun onEmailChange(email: String) {
        _email.value = email
    }

    fun onPasswordChange(password: String) {
        _password.value = password
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
                success = {
                    session.storeToken(it.token)
                    navigate(SignUpNavigationEvent.NavigateToHome)
                },
                onError = { setUiState(BaseUiState.Error(it)) }
            )
        }
    }

    fun onSignUpClick() {
        val request = SignUpRequest(fullName.value, email.value, password.value)
        handleApiCall(
            call = { foodApi.signUp(request) },
            success = {
                session.storeToken(it.token)
                navigate(SignUpNavigationEvent.NavigateToHome)
            },
            onError = { setUiState(BaseUiState.Error(it)) }
        )
    }

    fun onLoginCLick() {
        viewModelScope.launch {
            navigate(SignUpNavigationEvent.NavigateToLogin)
        }
    }

    fun onDialogDismissed() {
        _uiState.value = BaseUiState.Idle
    }

    sealed class SignUpNavigationEvent {
        object NavigateToLogin : SignUpNavigationEvent()
        object NavigateToHome : SignUpNavigationEvent()
    }
}