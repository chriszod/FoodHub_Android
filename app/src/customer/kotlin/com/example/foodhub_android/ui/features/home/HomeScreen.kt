package com.example.foodhub_android.ui.features.home

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Alignment.Companion.TopStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.foodhub_android.R
import com.example.foodhub_android.data.models.Category
import com.example.foodhub_android.data.models.Restaurant
import com.example.foodhub_android.navigation.RestaurantDetails
import com.example.foodhub_android.ui.theme.MyPrimaryColor
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    viewModel: HomeViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 250.dp)
    ) {
        LaunchedEffect(Unit) {
            viewModel.navigationEvent.collectLatest {
                when (it) {
                    is HomeViewModel.HomeNavigationEvent.NavigateToRestaurantDetails -> {
                        navController.navigate(
                            RestaurantDetails(
                                it.restaurantId,
                                it.name,
                                it.imageUrl
                            )
                        )
                    }

                    else -> {}
                }
            }
        }

        val uiState = viewModel.uiState.collectAsState()
        when (val state = uiState.value) {
            is HomeViewModel.HomeUiState.Success -> {
                val categories = viewModel.categories
                CategoriesList(categories = categories, onItemClick = {
                    navController.navigate("category/${it.id}")
                })

                val restaurant = viewModel.restaurants
                RestaurantList(
                    restaurant = restaurant,
                    sharedTransitionScope = sharedTransitionScope,
                    animatedVisibilityScope = animatedVisibilityScope,
                    onItemClick = { viewModel.onRestaurantClick(it) })
            }

            is HomeViewModel.HomeUiState.Error -> {
                Text(text = state.message)
            }

            is HomeViewModel.HomeUiState.Loading -> {
                Text(text = "Loading")
            }

            else -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }
    }
}

@Composable
fun CategoriesList(categories: List<Category>, onItemClick: (Category) -> Unit) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(categories) {
            CategoryItem(category = it, onItemClick = onItemClick)
        }
    }
}

@Composable
fun CategoryItem(category: Category, onItemClick: (Category) -> Unit) {
    Column(
        modifier = Modifier
            .height(100.dp)
            .width(60.dp)
            .clickable { onItemClick(category) }
            .shadow(
                16.dp,
                RoundedCornerShape(50.dp),
                ambientColor = Color.Gray.copy(alpha = 0.8f),
                spotColor = Color.Gray.copy(alpha = 0.8f)
            )
            .clip(RoundedCornerShape(50.dp))
            .background(color = Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(4.dp))
        AsyncImage(
            model = category.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = category.name, fontSize = 10.sp,
            textAlign = TextAlign.Center, maxLines = 1
        )
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RestaurantList(
    restaurant: List<Restaurant>,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (Restaurant) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Featured Restaurants", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        TextButton(onClick = { }) {
            Text(text = "View All >", fontSize = 20.sp, color = MyPrimaryColor)
        }
    }
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(restaurant) {
            RestaurantItem(
                restaurant = it,
                sharedTransitionScope,
                animatedVisibilityScope,
                onItemClick = onItemClick
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun RestaurantItem(
    restaurant: Restaurant,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (Restaurant) -> Unit
) {
    Card(
        modifier = Modifier
            .clickable { onItemClick(restaurant) }
            .height(229.dp)
            .width(266.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Box {
            Column(modifier = Modifier.fillMaxSize()) {
                with(sharedTransitionScope) {
                    AsyncImage(
                        contentDescription = null,
                        model = restaurant.imageUrl,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .weight(6f)
                            .sharedElement(
                                rememberSharedContentState(key = "image/${restaurant.id}"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(4f)
                        .padding(start = 16.dp)
                ) {
                    Spacer(modifier = Modifier.height(2.dp))
                    with(sharedTransitionScope) {
                        Text(
                            text = restaurant.name,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_rider),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = restaurant.name, fontSize = 12.sp)
                        Spacer(Modifier.width(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_timer),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(text = "10-15 mins", fontSize = 12.sp)
                    }
                    FlowRow {
                        listOf("Pizza", "Healthy", "Fast").forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(text = tag, color = Color.Gray) },
                                modifier = Modifier.padding(end = 8.dp, bottom = 8.dp),
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = Color.LightGray.copy(alpha = 0.1f),
                                    labelColor = Color.Black
                                ),
                                border = AssistChipDefaults.assistChipBorder(false)
                            )
                        }
                    }
                }
            }
            Text(
                text = "4.5 ⭐(25+)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier
                    .padding(start = 12.dp, top = 12.dp)
                    .background(Color.White, shape = RoundedCornerShape(16.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
                    .align(TopStart)
            )
            IconButton(
                onClick = {},
                modifier = Modifier
                    .padding(end = 12.dp, top = 12.dp)
                    .align(TopEnd)
                    .background(color = MyPrimaryColor, shape = CircleShape)
                    .size(32.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_favourite),
                    contentDescription = null,
                    modifier = Modifier
                        .size(20.dp)
                )
            }
        }
    }
}