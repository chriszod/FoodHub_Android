package com.example.foodhub_android.ui.features.login

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_android.R
import com.example.foodhub_android.navigation.Home
import com.example.foodhub_android.navigation.SignUp
import com.example.foodhub_android.ui.BasicDialog
import com.example.foodhub_android.ui.CustomEditText
import com.example.foodhub_android.ui.GroupSocialsButton
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel.BaseUiState
import com.example.foodhub_android.ui.theme.Orange

@Composable
fun LoginScreen(viewModel: LoginViewModel = hiltViewModel(), navController: NavController) {

    Box(modifier = Modifier.fillMaxSize()) {
        val email = viewModel.email.collectAsStateWithLifecycle()
        val password = viewModel.password.collectAsStateWithLifecycle()

        val errorMessage = remember { mutableStateOf<String?>(null) }
        val successMessage = remember { mutableStateOf<String?>(null) }
        val loading = remember { mutableStateOf(false) }

        val uiState = viewModel.uiState.collectAsState()
        when (val state = uiState.value) {
            is BaseUiState.Success -> {
                successMessage.value = "Success"
                loading.value = false
            }

            is BaseUiState.Error -> {
                errorMessage.value = state.message ?: "Unknown error"
                loading.value = false
            }

            is BaseUiState.Loading -> {
                loading.value = true
                errorMessage.value = null
                successMessage.value = null
            }

            else -> {
                loading.value = false
            }
        }

        if (errorMessage.value != null) {
            BasicDialog(
                title = "Error",
                description = errorMessage.value!!,
                onDismiss = {
                    errorMessage.value = null
                    viewModel.onDialogDismissed()
                }
            )
        }

        val context = LocalContext.current
        LaunchedEffect(key1 = true) {
            viewModel.navigationEvent.collect {
                when (it) {
                    is LoginViewModel.LoginNavigationEvent.NavigateToSignup -> {
                        navController.navigate(SignUp)
                    }
                    is LoginViewModel.LoginNavigationEvent.NavigateToHome -> {
                        navController.navigate(Home)
                    }
                }
            }
        }


        Image(
            painter = painterResource(id = R.drawable.signup),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(top = 220.dp, start = 32.dp, end = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.login),
                color = Color.Black,
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            CustomEditText(
                value = email.value,
                onValueChange = { viewModel.onEmailChange(it) },
                label = { Text(text = stringResource(id = R.string.email), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
            )
            CustomEditText(
                value = password.value,
                onValueChange = { viewModel.onPasswordChange(it) },
                label = { Text(text = stringResource(id = R.string.password), color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                trailingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.ic_eye),
                        contentDescription = "Eye Icon",
                        modifier = Modifier.size(24.dp)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(id = R.string.forgot_password),
                color = Orange,
                fontSize = 16.sp,
                modifier = Modifier.clickable(onClick = {})
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage.value ?: "", color = Color.Red, fontSize = 16.sp)
            Button(
                onClick = viewModel::onLoginClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Orange
                ),
                modifier = Modifier.height(55.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AnimatedContent(
                        targetState = loading.value,
                        transitionSpec = {
                            fadeIn(animationSpec = tween(durationMillis = 300)) +
                                    scaleIn(initialScale = 0.8f) togetherWith fadeOut(
                                animationSpec = tween(
                                    durationMillis = 300
                                )
                            ) +
                                    scaleOut(targetScale = 0.8f)
                        }
                    ) {
                        if (it) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(24.dp))
                        } else {
                            Text(
                                text = stringResource(id = R.string.login_caps),
                                color = Color.White,
                                fontSize = 20.sp,
                            )
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
            Row {
                Text(
                    text = stringResource(id = R.string.not_have_account),
                    color = Color.Black,
                    fontSize = 16.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(id = R.string.signup),
                    color = Orange,
                    fontSize = 16.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = viewModel::onSignupCLick)
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
            GroupSocialsButton(color = Color.Black, onFacebookClick = {}, onGoogleClick = {viewModel.onGoogleClick(context)})
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginScreen(navController = rememberNavController())
}