package com.example.foodhub_android.ui.features.food_item_details

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.foodhub_android.R
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.ui.BasicDialog
import com.example.foodhub_android.ui.features.restaurant_details.RestaurantDetailsBody
import com.example.foodhub_android.ui.features.restaurant_details.RestaurantDetailsHeader
import com.example.foodhub_android.ui.theme.Orange
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodItemDetailsScreen(
    navController: NavController,
    foodItem: FoodItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: FoodDetailsViewModel = hiltViewModel()
) {
    val price = foodItem.price.toString()
    val count = viewModel.quantity.collectAsStateWithLifecycle()
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val isLoading = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.event.collectLatest {
            when (it) {
                is FoodDetailsViewModel.FoodDetailsEvent.onAddToCart -> {
                    Toast.makeText(navController.context, "Added to cart", Toast.LENGTH_SHORT)
                        .show()
                }

                is FoodDetailsViewModel.FoodDetailsEvent.goToCart -> {
                    Toast.makeText(navController.context, "Go to cart", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    when (val state = uiState.value) {
        is FoodDetailsViewModel.FoodDetailsUiState.Loading -> {
            isLoading.value = true
        }

        is FoodDetailsViewModel.FoodDetailsUiState.Error -> {
            BasicDialog("Error", state.message, onDismiss = {
                viewModel.onDialogDismissed()
            })
            isLoading.value = false
        }

        else -> {
            isLoading.value = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        RestaurantDetailsHeader(
            imageUrl = foodItem.imageUrl,
            restaurantId = foodItem.id ?: "",
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onBackButtonClick = { navController.popBackStack() },
            onFavoriteClick = { }
        )
        Spacer(Modifier.height(16.dp))

        RestaurantDetailsBody(
            title = foodItem.name,
            description = foodItem.description,
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "$",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Orange
            )
            Text(
                text = price,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Orange
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Orange, CircleShape)
                        .clip(CircleShape)
                ) {
                    IconButton(
                        onClick = { viewModel.decreaseQuantity() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_minus),
                            contentDescription = null
                        )
                    }
                }
                Text(
                    text = count.value.toString(),
                    style = TextStyle(fontSize = 16.sp),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Orange, CircleShape)
                        .clip(CircleShape)
                ) {
                    IconButton(
                        onClick = { viewModel.increaseQuantity() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_add),
                            contentDescription = null
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(color = Orange, shape = CircleShape)
                .padding(4.dp)
                .align(Alignment.CenterHorizontally)
                .clickable(onClick = {
                    viewModel.addToCart(
                        foodItem.restaurantId,
                        foodItem.id.toString()
                    )
                })
        ) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_bag),
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Inside,
                        contentDescription = null,
                    )
                }
                Text(
                    text = "ADD TO CART",
                    style = TextStyle(
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}