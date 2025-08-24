package com.example.foodhub_android.navigation
//
//import androidx.compose.animation.AnimatedVisibility
//import androidx.compose.animation.ExperimentalSharedTransitionApi
//import androidx.compose.animation.SharedTransitionLayout
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.toRoute
//import com.example.foodhub_android.HomeViewModel
//import com.example.foodhub_android.data.models.FoodItem
//import com.example.foodhub_android.data.remote.SessionViewModel
//import com.example.foodhub_android.ui.features.add_address.AddAddressScreen
//import com.example.foodhub_android.ui.features.address.AddressListScreen
//import com.example.foodhub_android.ui.features.auth.AuthScreen
//import com.example.foodhub_android.ui.features.cart.CartScreen
//import com.example.foodhub_android.ui.features.cart.CartViewModel
//import com.example.foodhub_android.ui.features.food_item_details.FoodItemDetailsScreen
//import com.example.foodhub_android.ui.features.home.HomeScreen
//import com.example.foodhub_android.ui.features.login.LoginScreen
//import com.example.foodhub_android.ui.features.notification.NotificationListScreen
//import com.example.foodhub_android.ui.features.notification.NotificationsViewModel
//import com.example.foodhub_android.ui.features.order.OrderDetailsScreen
//import com.example.foodhub_android.ui.features.order.OrderListScreen
//import com.example.foodhub_android.ui.features.order.OrderSuccessScreen
//import com.example.foodhub_android.ui.features.restaurant_details.RestaurantDetailsScreen
//import com.example.foodhub_android.ui.features.signup.SignUpScreen
//import kotlinx.coroutines.flow.collectLatest
//import kotlin.reflect.typeOf
//
//@OptIn(ExperimentalSharedTransitionApi::class)
//@Composable
//fun AppNavHost(
//    navController: NavHostController = rememberNavController(),
//    viewModel: HomeViewModel
//) {
//    val sessionViewModel: SessionViewModel = hiltViewModel()
//    val isLoggedIn = sessionViewModel.hasToken()
//
//    // Declare it outside, but only initialize if logged in
//    val cartViewModel: CartViewModel? = if (isLoggedIn) {
//        hiltViewModel()
//    } else {
//        null
//    }
//    val cartItemSize = cartViewModel?.cartItemCount?.collectAsStateWithLifecycle()
//
//    val notificationViewModel: NotificationsViewModel = hiltViewModel()
//    val unreadCount = notificationViewModel.unreadCount.collectAsStateWithLifecycle()
//
//    val shouldShowBottomNav = remember { mutableStateOf(false) }
//
//    LaunchedEffect(key1 = true) {
//        viewModel.event.collectLatest {
//            when (it) {
//                is HomeViewModel.HomeEvent.NavigateToOrderDetail -> {
//                    navController.navigate(OrderDetails(it.orderID))
//                }
//            }
//        }
//    }
//
//    SharedTransitionLayout {
//        Scaffold(
//            bottomBar = {
//                AnimatedVisibility(visible = shouldShowBottomNav.value) {
//                    BottomNavigationBar(
//                        navController = navController,
//                        cartItemSize = cartItemSize,
//                        unreadCount = unreadCount
//                    )
//                }
//            }
//        ) { innerPadding ->
//            NavHost(
//                navController = navController,
//                startDestination = if (isLoggedIn) Home else Auth,
//                modifier = Modifier.padding(innerPadding)
//            ) {
//                composable<Auth> {
//                    shouldShowBottomNav.value = false
//                    AuthScreen(navController = navController)
//                }
//                composable<SignUp> {
//                    shouldShowBottomNav.value = false
//                    SignUpScreen(navController = navController)
//                }
//                composable<Login> {
//                    shouldShowBottomNav.value = false
//                    LoginScreen(navController = navController)
//                }
//                composable<Home> {
//                    shouldShowBottomNav.value = true
//                    HomeScreen(
//                        navController = navController,
//                        sharedTransitionScope = this@SharedTransitionLayout,
//                        animatedVisibilityScope = this
//                    )
//                }
//                composable<Cart> {
//                    shouldShowBottomNav.value = true
//                    cartViewModel?.let {
//                        CartScreen(navController = navController, viewModel = it)
//                    }
//                }
//                composable<Notification> {
//                    shouldShowBottomNav.value = true
//                    NotificationListScreen(navController, notificationViewModel)
//                }
//                composable<RestaurantDetails> {
//                    shouldShowBottomNav.value = false
//                    val route = it.toRoute<RestaurantDetails>()
//                    RestaurantDetailsScreen(
//                        navController = navController,
//                        name = route.restaurantName,
//                        imageUrl = route.restaurantImageUrl,
//                        restaurantId = route.restaurantId,
//                        sharedTransitionScope = this@SharedTransitionLayout,
//                        animatedVisibilityScope = this
//                    )
//                }
//                composable<FoodItemDetails>(
//                    typeMap = mapOf(typeOf<FoodItem>() to foodItemNavType)
//                ) {
//                    shouldShowBottomNav.value = false
//                    val route = it.toRoute<FoodItemDetails>()
//                    cartViewModel?.let { vm ->
//                        FoodItemDetailsScreen(
//                            navController = navController,
//                            foodItem = route.foodItem,
//                            sharedTransitionScope = this@SharedTransitionLayout,
//                            animatedVisibilityScope = this,
//                            onItemAddedToCart = { vm.getCart() }
//                        )
//                    }
//                }
//                composable<OrderList> {
//                    shouldShowBottomNav.value = true
//                    Box(modifier = Modifier.background(Color.White)) {}
//                }
//                composable<AddressList> {
//                    shouldShowBottomNav.value = false
//                    AddressListScreen(navController)
//                }
//                composable<AddAddress> {
//                    shouldShowBottomNav.value = false
//                    AddAddressScreen(navController)
//                }
//                composable<OrderSuccess> {
//                    shouldShowBottomNav.value = false
//                    val orderId = it.toRoute<OrderSuccess>().orderId
//                    OrderSuccessScreen(orderId, navController)
//                }
//                composable<OrderList> {
//                    shouldShowBottomNav.value = true
//                    OrderListScreen(navController)
//                }
//                composable<OrderDetails> {
//                    shouldShowBottomNav.value = false
//                    val orderId = it.toRoute<OrderDetails>().orderId
//                    OrderDetailsScreen(navController, orderId)
//                }
//            }
//        }
//    }
//}