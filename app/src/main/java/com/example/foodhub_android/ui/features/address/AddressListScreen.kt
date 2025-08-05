package com.example.foodhub_android.ui.features.address

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_android.R
import com.example.foodhub_android.navigation.AddAddress
import com.example.foodhub_android.ui.features.cart.AddressCard
import com.example.foodhub_android.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@Composable
fun AddressListScreen(
    navController: NavController,
    viewModel: AddressListViewModel = hiltViewModel()
) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (val addressEvent = it) {
                is AddressListViewModel.AddressEvent.NavigateToEditAddress -> {
                    // Navigate to edit address screen
                }

                is AddressListViewModel.AddressEvent.NavigateToAddAddress -> {
                    navController.navigate(AddAddress)
                }

                is AddressListViewModel.AddressEvent.NavigateBack -> {
                    val address = addressEvent.address
                    navController.previousBackStackEntry?.savedStateHandle?.set("address", address)
                    navController.popBackStack()
                }

                else -> {

                }
            }
        }
    }

    val isAddressAdded =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow("isAddressAdded", false)
            ?.collectAsState(false)
    LaunchedEffect(key1 = isAddressAdded?.value) {
        if (isAddressAdded?.value == true) {
            viewModel.getAddress()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = navController::popBackStack,
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
            Text(text = "Address List", style = MaterialTheme.typography.titleMedium)
            IconButton(
                onClick = { viewModel.onAddAddressClicked() },
                modifier = Modifier
                    .shadow(4.dp, shape = RoundedCornerShape(16.dp))
                    .background(color = Orange, shape = RoundedCornerShape(16.dp)),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        when (val addressState = state.value) {
            is AddressListViewModel.AddressState.Loading -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Show loading
                    CircularProgressIndicator()
                    Text(
                        text = "Loading...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            is AddressListViewModel.AddressState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxSize()
                ) {
                    items(addressState.data) {
                        AddressCard(address = it, onAddressClicked = {
                            viewModel.onAddressSelected(it)
                        })
                    }
                }
            }

            is AddressListViewModel.AddressState.Error -> {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Show loading
                    Text(
                        text = addressState.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                    Button(onClick = { viewModel.getAddress() }) {
                        Text(text = "Retry")
                    }
                }
            }
        }
    }
}