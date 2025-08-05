package com.example.foodhub_android.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.data.remote.FoodHubSession
import com.example.foodhub_android.ui.features.add_address.AddAddressScreen
import com.example.foodhub_android.ui.features.address.AddressListScreen
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.features.cart.CartScreen
import com.example.foodhub_android.ui.features.cart.CartViewModel
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
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isLoggedIn = sessionViewModel.hasToken()

    val shouldShowBottomNav = remember {
        mutableStateOf(false)
    }

    val cartViewModel: CartViewModel = hiltViewModel()
    val cartItemSize = cartViewModel.cartItemCount.collectAsStateWithLifecycle()


    SharedTransitionLayout {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(visible = shouldShowBottomNav.value) {
                    BottomNavigationBar(navController = navController, cartItemSize = cartItemSize)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = if (isLoggedIn) Home else Auth,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable<Auth> {
                    shouldShowBottomNav.value = false
                    AuthScreen(navController = navController)
                }
                composable<SignUp> {
                    shouldShowBottomNav.value = false
                    SignUpScreen(navController = navController)
                }
                composable<Login> {
                    shouldShowBottomNav.value = false
                    LoginScreen(navController = navController)
                }
                composable<Home> {
                    shouldShowBottomNav.value = true
                    HomeScreen(
                        navController = navController,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
                composable<Cart> {
                    shouldShowBottomNav.value = true
                    CartScreen(navController = navController, viewModel = cartViewModel)
                }
                composable<Notification> {
                    shouldShowBottomNav.value = true
                    Box {

                    }
                }
                composable<RestaurantDetails> {
                    shouldShowBottomNav.value = false
                    val route = it.toRoute<RestaurantDetails>()
                    RestaurantDetailsScreen(
                        navController = navController,
                        name = route.restaurantName,
                        imageUrl = route.restaurantImageUrl,
                        restaurantId = route.restaurantId,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this
                    )
                }
                composable<FoodItemDetails>(
                    typeMap = mapOf(
                        typeOf<FoodItem>() to foodItemNavType
                    )
                ) {
                    shouldShowBottomNav.value = false
                    val route = it.toRoute<FoodItemDetails>()
                    FoodItemDetailsScreen(
                        navController = navController,
                        foodItem = route.foodItem,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = this,
                        onItemAddedToCart = {
                            cartViewModel.getCart()
                        }
                    )
                }
                composable<OrderList> {
                    shouldShowBottomNav.value = true
                    Box(modifier = Modifier.background(Color.White)) {

                    }
                }
                composable<AddressList> {
                    shouldShowBottomNav.value = false
                    AddressListScreen(navController)
                }
                composable<AddAddress> {
                    shouldShowBottomNav.value = false
                    AddAddressScreen(navController)
                }
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