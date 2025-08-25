package com.example.foodhub_android.ui.features.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import coil3.compose.AsyncImage
import com.example.foodhub_android.R
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.ui.theme.MyPrimaryColor

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
                    .background(color = MyPrimaryColor, shape = CircleShape)
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