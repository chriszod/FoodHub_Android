package com.example.foodhub_android.navigation

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.data.remote.FoodHubSession
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.features.cart.CartScreen
import com.example.foodhub_android.ui.features.food_item_details.FoodItemDetailsScreen
import com.example.foodhub_android.ui.features.home.HomeScreen
import com.example.foodhub_android.ui.features.login.LoginScreen
import com.example.foodhub_android.ui.features.restaurant_details.RestaurantDetailsScreen
import com.example.foodhub_android.ui.features.signup.SignUpScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.reflect.typeOf

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(navController: NavHostController = rememberNavController(), modifier: Modifier) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isLoggedIn = sessionViewModel.hasToken()
    SharedTransitionLayout {
        NavHost(navController, startDestination = if (isLoggedIn) Home else Auth) {
            composable<Auth> { AuthScreen(navController = navController) }
            composable<SignUp> { SignUpScreen(navController = navController) }
            composable<Login> { LoginScreen(navController = navController) }
            composable<Cart> { CartScreen(navController = navController) }
            composable<Home> {
                HomeScreen(
                    navController = navController,
                    this@SharedTransitionLayout,
                    this
                )
            }
            composable<RestaurantDetails> {
                val route = it.toRoute<RestaurantDetails>()
                RestaurantDetailsScreen(
                    navController = navController,
                    name = route.restaurantName,
                    imageUrl = route.restaurantImageUrl,
                    restaurantId = route.restaurantId,
                    this@SharedTransitionLayout,
                    this
                )
            }
            composable<FoodItemDetails>(
                typeMap = mapOf(
                    typeOf<FoodItem>() to foodItemNavType
                )
            ) {
                val route = it.toRoute<FoodItemDetails>()
                FoodItemDetailsScreen(
                    navController = navController,
                    foodItem = route.foodItem,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this
                )
            }
        }
    }
}

@HiltViewModel
class SessionViewModel @Inject constructor(
    private val session: FoodHubSession
) : ViewModel() {
    fun hasToken(): Boolean = session.getToken() != null
}