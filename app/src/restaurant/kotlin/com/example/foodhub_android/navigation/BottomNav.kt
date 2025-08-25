package com.example.foodhub_android.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.foodhub_android.R
import com.example.foodhub_android.ui.theme.Mustard
import com.example.foodhub_android.ui.theme.MyPrimaryColor

sealed class BottomNavItem(val route: NavRoute, val icon: Int) {
    object Home : BottomNavItem(com.example.foodhub_android.navigation.Home, R.drawable.ic_home)
    object Orders : BottomNavItem(OrderList, R.drawable.ic_orders)
    object Notification : BottomNavItem(com.example.foodhub_android.navigation.Notification, R.drawable.ic_notification)
    object Menu : BottomNavItem(MenuList, R.drawable.ic_bag)
}

@Composable
fun BottomNavigationBar(navController: NavController, unreadCount: State<Int>) {
    val currentRoute =
        navController.currentBackStackEntryAsState().value?.destination

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Orders,
        BottomNavItem.Notification,
        BottomNavItem.Menu
    )
    NavigationBar(
        containerColor = Color.White
    ) {
        items.forEach { item ->
            val selected =
                currentRoute?.hierarchy?.any { it.route == item.route::class.qualifiedName } == true
            NavigationBarItem(
                icon = {
                    Box(modifier = Modifier.size(48.dp)) {
                        Icon(
                            modifier = Modifier.size(24.dp).align(Alignment.Center),
                            painter = painterResource(id = item.icon),
                            contentDescription = null
                        )
                        if(item.route == Notification && unreadCount.value > 0) {
                            ItemCountRes(unreadCount.value)
                        }
                    }
                },
                selected = selected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MyPrimaryColor,
                    unselectedIconColor = Color.Gray,
                    indicatorColor = Color.Transparent
                ),
                onClick = {
                    navController.navigate(item.route)
                }
            )
        }
    }
}

@Composable
fun BoxScope.ItemCountRes(count: Int) {
    Box(
        modifier = Modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(Mustard)
            .align(Alignment.TopEnd)
    ) {
        Text(
            text = "$count",
            modifier = Modifier
                .align(Alignment.Center),
            color = Color.White,
            style = TextStyle(fontSize = 10.sp)
        )
    }
}