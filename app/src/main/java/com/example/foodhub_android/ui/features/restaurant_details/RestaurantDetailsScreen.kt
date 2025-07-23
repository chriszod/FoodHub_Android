package com.example.foodhub_android.ui.features.restaurant_details

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.foodhub_android.R
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.navigation.FoodItemDetails
import com.example.foodhub_android.ui.theme.Orange

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RestaurantDetailsScreen(
    navController: NavController,
    name: String,
    imageUrl: String,
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: RestaurantViewModel = hiltViewModel()
) {
    LaunchedEffect(restaurantId) {
        viewModel.getFoodItems(restaurantId)
    }
    val uiState = viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        RestaurantDetailsHeader(
            imageUrl = imageUrl,
            restaurantId = restaurantId,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = animatedVisibilityScope,
            onBackButtonClick = { navController.popBackStack() },
            onFavoriteClick = { }
        )
        Spacer(Modifier.height(16.dp))

        RestaurantDetailsBody(
            title = name,
            description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.",
        )
        Spacer(Modifier.height(16.dp))

        when (val state = uiState.value) {
            is RestaurantViewModel.RestaurantUiState.Success -> {
                val foodItems = state.foodItems
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(24.dp),
                    horizontalArrangement = Arrangement.spacedBy(24.dp),
                ) {
                    items(foodItems.size) {
                        FoodItemView(
                            foodItem = foodItems[it],
                            sharedTransitionScope = sharedTransitionScope,
                            animatedVisibilityScope = animatedVisibilityScope,
                            onItemClick = {
                                navController.navigate(FoodItemDetails(it))
                            }
                        )
                    }
                }
            }

            else -> {
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RestaurantDetailsHeader(
    imageUrl: String,
    restaurantId: String,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackButtonClick: () -> Unit,
    onFavoriteClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
    ) {
        with(sharedTransitionScope) {
            AsyncImage(
                contentDescription = null,
                model = imageUrl,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedElement(
                        rememberSharedContentState(key = "image/$restaurantId"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
            )
        }
        IconButton(
            onClick = onBackButtonClick,
            modifier = Modifier
                .padding(start = 16.dp, top = 16.dp)
                .align(TopStart)
                .background(color = Color.White, shape = RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
        }
        IconButton(
            onClick = onBackButtonClick,
            modifier = Modifier
                .padding(end = 20.dp, top = 20.dp)
                .align(TopEnd)
                .background(color = Orange, shape = CircleShape)
                .size(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_favourite),
                contentDescription = null,
                modifier = Modifier
                    .size(18.dp)
            )
        }
    }
}

@Composable
fun RestaurantDetailsBody(
    title: String,
    description: String,
) {
    Column {
        Text(
            text = title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(imageVector = Icons.Default.Star, tint = Color.Yellow, contentDescription = "Star")
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "4.5", fontSize = 12.sp)
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "(30+)", color = Color.Gray, fontSize = 12.sp)
            TextButton(onClick = {}) { Text(text = "See Review", fontSize = 12.sp, color = Orange) }
        }
        Text(text = description, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun FoodItemView(
    foodItem: FoodItem,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (FoodItem) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = { onItemClick.invoke(foodItem) })
    ) {
        Box {
            with (sharedTransitionScope) {
                AsyncImage(
                    model = foodItem.imageUrl, contentDescription = null,
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image/${foodItem.id}"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .aspectRatio(1f),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = "$${foodItem.price}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .align(TopStart)
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .padding(end = 8.dp, top = 8.dp)
                    .align(TopEnd)
                    .background(color = Orange, shape = CircleShape)
                    .size(24.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_favourite),
                    contentDescription = null,
                    modifier = Modifier
                        .size(16.dp)
                )
            }
            Text(
                text = "4.5 ⭐(25+)",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .offset(y = 12.dp)
                    .padding(start = 12.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                    .align(BottomStart)
            )
        }
        Spacer(Modifier.height(8.dp))
        Text(
            text = foodItem.name,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(start = 16.dp)
        )
        Text(
            text = foodItem.description,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            color = Color.Gray,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}