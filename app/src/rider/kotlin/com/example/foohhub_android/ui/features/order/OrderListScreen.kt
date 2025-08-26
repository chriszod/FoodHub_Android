package com.example.foohhub_android.ui.features.order

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_android.navigation.OrderDetails
import com.example.foodhub_android.ui.features.notification.ErrorScreen
import com.example.foodhub_android.ui.features.notification.LoadingScreen
import com.example.foodhub_android.utils.StringUtils

@Composable
fun OrderListScreen(
    navController: NavController,
    viewModel: OrderListViewModel = hiltViewModel()
) {
    Column(modifier = Modifier.fillMaxSize()) {
        val state = viewModel.state.collectAsStateWithLifecycle()

        when (state.value) {
            is OrderListViewModel.OrderListState.Loading -> {
                LoadingScreen()
            }

            is OrderListViewModel.OrderListState.Empty -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No orders available",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    )
                }
            }

            is OrderListViewModel.OrderListState.Success -> {
                val orders = (state.value as OrderListViewModel.OrderListState.Success).orders
                LazyColumn {
                    items(orders) { delivery ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                                .shadow(4.dp, shape = RoundedCornerShape(8.dp))
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                                .clickable {
                                    navController.navigate(OrderDetails(delivery.orderId))
                                }
                                .padding(8.dp)
                        ) {
                            Text(
                                text = delivery.customer.addressLine1,
                                color = Color.Black,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = delivery.restaurant.address,
                                color = Color.Black,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = delivery.orderId,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = delivery.status,
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = StringUtils.formatCurrency(delivery.estimatedEarning),
                                color = Color.Green
                            )
                        }
                    }
                }
            }

            is OrderListViewModel.OrderListState.Error -> {
                ErrorScreen((state.value as OrderListViewModel.OrderListState.Error).message) {
                    viewModel.getOrders()
                }
            }
        }
    }
}