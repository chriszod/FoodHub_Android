package com.example.foodhub_android.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodhub_android.HomeViewModel
import com.example.foodhub_android.data.remote.SessionViewModel
import com.example.foodhub_android.ui.features.auth.AuthScreen
import com.example.foodhub_android.ui.features.login.LoginScreen
import com.example.foodhub_android.ui.features.notification.NotificationListScreen
import com.example.foodhub_android.ui.features.notification.NotificationsViewModel
import com.example.foodhub_android.ui.features.signup.SignUpScreen
import com.example.foohhub_android.ui.features.home.DeliveriesScreen
import com.example.foohhub_android.ui.features.order.OrderDetailsScreen
import com.example.foohhub_android.ui.features.order.OrderListScreen
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    viewModel: HomeViewModel
) {
    val sessionViewModel: SessionViewModel = hiltViewModel()
    val isLoggedIn = sessionViewModel.hasToken()

    val notificationViewModel: NotificationsViewModel = hiltViewModel()
    val unreadCount = notificationViewModel.unreadCount.collectAsStateWithLifecycle()

    val shouldShowBottomNav = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                is HomeViewModel.HomeEvent.NavigateToOrderDetail -> {
                    navController.navigate(OrderDetails(it.orderID))
                }
            }
        }
    }

    SharedTransitionLayout {
        Scaffold(
            bottomBar = {
                AnimatedVisibility(visible = shouldShowBottomNav.value) {
                    BottomNavigationBar(
                        navController = navController,
                        unreadCount = unreadCount
                    )
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
                    DeliveriesScreen(
                        navController = navController
                    )
                }
                composable<Notification> {
                    shouldShowBottomNav.value = true
                    NotificationListScreen(navController, notificationViewModel)
                }
                composable<OrderList> {
                    shouldShowBottomNav.value = true
                    OrderListScreen(navController)
                }
                composable<OrderDetails> {
                    shouldShowBottomNav.value = false
                    val orderId = it.toRoute<OrderDetails>().orderId
                    OrderDetailsScreen(orderId,navController)
                }
            }
        }
    }
}