package com.example.foodhub_android.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.features.home.HomeScreen
import com.example.foodhub_android.ui.features.login.LoginScreen
import com.example.foodhub_android.ui.features.signup.SignUpScreen

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), modifier: Modifier) {
    NavHost(navController, startDestination = Home) {
        composable<Auth> { AuthScreen(navController = navController) }
        composable<SignUp>{ SignUpScreen(navController = navController) }
        composable<Login>{ LoginScreen(navController = navController) }
        composable<Home>{ HomeScreen(navController = navController) }
    }
}