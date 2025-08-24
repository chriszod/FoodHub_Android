package com.example.foodhub_android.ui.features.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_android.R
import com.example.foodhub_android.navigation.Home
import com.example.foodhub_android.navigation.Login
import com.example.foodhub_android.navigation.SignUp
import com.example.foodhub_android.ui.BasicDialog
import com.example.foodhub_android.ui.GroupSocialsButton
import com.example.foodhub_android.ui.features.base.BaseAuthViewModel.BaseUiState
import com.example.foodhub_android.ui.theme.BitBlack
import com.example.foodhub_android.ui.theme.MyPrimaryColor

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel(), navController: NavController) {
    var size by remember { mutableStateOf(IntSize.Zero) }

    Box(modifier = Modifier.fillMaxSize()) {
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
                errorMessage.value = state.message ?: "Unknown Error"
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
                    is AuthViewModel.AuthNavigationEvent.NavigateToSignup -> {
                        navController.navigate(SignUp)
                    }
                    is AuthViewModel.AuthNavigationEvent.NavigateToLogin -> {
                        navController.navigate(Login)
                    }
                    is AuthViewModel.AuthNavigationEvent.NavigateToHome -> {
                        navController.navigate(Home)
                    }
                }
            }
        }


        Image(painter = painterResource(id = R.drawable.welcomebackground),
            contentDescription = null,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    size = coordinates.size // IntSize(width, height) in pixels
                }
                .alpha(0.6f),
            contentScale = androidx.compose.ui.layout.ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF000000)
                        ),
                        startY = size.height.toFloat() / 3
                    )
                )
        )

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
            ),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(20.dp)
        ) {
            Text(text = stringResource(id = R.string.skip), color = MyPrimaryColor)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 180.dp, start = 32.dp, end = 32.dp)) {
            Text(
                text = stringResource(id = R.string.welcome_to),
                color = Color.Black,
                fontSize = 55.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.food_hub),
                color = MyPrimaryColor,
                fontSize = 50.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.welcome_detail),
                color = BitBlack,
                fontSize = 20.sp,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 50.dp, start = 32.dp, end = 32.dp)
                .align(Alignment.BottomCenter)
        ) {
            GroupSocialsButton(onFacebookClick = {}, onGoogleClick = { viewModel.onGoogleClick(context) })

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = viewModel::onSignupCLick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f),
                ),
                border = BorderStroke(1.dp, Color.White),
                modifier = Modifier.height(55.dp).fillMaxWidth()) {
                Text(text = stringResource(id = R.string.start_with_email),
                    color = Color.White, fontSize = 20.sp)
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                ) {
                Text(
                    text = "Already have an account?",
                    color = Color.White,
                    fontSize = 20.sp,
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Sign in",
                    color = Color.White,
                    fontSize = 20.sp,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable(onClick = viewModel::onLoginCLick)
                )
            }
        }
    }
}

@Preview
@Composable
fun AuthScreenPreview() {
    AuthScreen(navController = rememberNavController())
}