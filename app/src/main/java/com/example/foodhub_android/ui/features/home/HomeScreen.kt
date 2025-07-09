package com.example.foodhub_android.ui.features.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.foodhub_android.data.models.Category
import com.example.foodhub_android.ui.theme.Orange

@Composable
fun HomeScreen(navController: NavController, viewModel: HomeViewModel = hiltViewModel()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        val uiState = viewModel.uiState.collectAsState()
        when (val state = uiState.value) {
            is HomeViewModel.HomeUiState.Success -> {
                val categories = viewModel.categories
                CategoriesList(categories = categories, onItemClick = {
                    navController.navigate("category/${it.id}")
                })
            }

            is HomeViewModel.HomeUiState.Error -> {
                Text(text = state.message)
            }

            is HomeViewModel.HomeUiState.Loading -> {
                Text(text = "Loading")
            }

            else -> {
                Text(text = "Unknown")
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
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
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

@Preview
@Composable
fun CategoryItemPreview() {
    CategoryItem(category = Category("", "", "", ""), onItemClick = {})
}