package com.example.foodhub_android.ui.features.cart

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.foodhub_android.R
import com.example.foodhub_android.StringUtils
import com.example.foodhub_android.data.models.Address
import com.example.foodhub_android.data.models.CartItem
import com.example.foodhub_android.data.models.CheckoutDetails
import com.example.foodhub_android.navigation.AddressList
import com.example.foodhub_android.navigation.OrderSuccess
import com.example.foodhub_android.ui.BasicDialog
import com.example.foodhub_android.ui.theme.Orange
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet

@Composable
fun CartScreen(navController: NavController, viewModel: CartViewModel = hiltViewModel()) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle()

    val showDialog = remember {
        mutableStateOf(false)
    }
    if (showDialog.value) {
        BasicDialog("Error", "Something went wrong")
    }

    val paymentSheet = rememberPaymentSheet(
        paymentResultCallback = {
            if (it is PaymentSheetResult.Completed) {
                viewModel.onPaymentSuccess()
            } else {
                viewModel.onPaymentFailure()
            }
        }
    )

    LaunchedEffect(Unit) {
        viewModel.event.collect {
            when (it) {
                is CartViewModel.CartNavigationEvent.OrderSuccess -> {
                    navController.navigate(OrderSuccess(it.orderId!!))
                }
                is CartViewModel.CartNavigationEvent.OnInitiatePayment -> {
                    PaymentConfiguration.init(navController.context, it.data.publishableKey)
                    val customer = PaymentSheet.CustomerConfiguration(
                        it.data.customerId,
                        it.data.ephemeralKeySecret
                    )
                    val paymentSheetConfig = PaymentSheet.Configuration(
                        merchantDisplayName = "FoodHub",
                        customer = customer,
                        allowsDelayedPaymentMethods = false
                    )
                    paymentSheet.presentWithPaymentIntent(
                        it.data.paymentIntentClientSecret,
                        paymentSheetConfig
                    )
                }
                is CartViewModel.CartNavigationEvent.ShowErrorDialog -> {
                    showDialog.value = true
                }

                is CartViewModel.CartNavigationEvent.OnAddressClicked -> {
                    navController.navigate(AddressList)
                }
                else -> {}
            }
        }
    }

    val address =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>(
            "address",
            null
        )?.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.onAddressSelected(it)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Spacer(Modifier.height(16.dp))
        CartScreenHeader(onBackClick = { navController.popBackStack() })

        when (val state = uiState.value) {
            is CartViewModel.CartUiState.Success -> {
                val data = state.data
                if (data.items.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_bag),
                            contentDescription = null,
                            tint = Color.Gray
                        )
                        Text(
                            text = "Your cart is empty",
                            style = TextStyle(fontSize = 20.sp, color = Color.Gray),
                        )
                    }
                } else {
                    LazyColumn {
                        items(data.items) {
                            CartScreenBody(
                                cartItem = it,
                                onItemCancelClick = { viewModel.removeItem(it) },
                                onIncreaseClick = { viewModel.increaseQuantity(it) },
                                onDecreaseClick = { viewModel.decreaseQuantity(it) }
                            )
                        }

                        item {
                            Spacer(Modifier.height(36.dp))
                            PromoCodeInput(
                                promoCode = "",
                                onPromoCodeChange = { },
                                onApplyClick = { }
                            )
                        }

                        item {
                            Spacer(Modifier.height(36.dp))
                            CartCostSummary(
                                itemCount = data.items.size,
                                checkoutDetails = data.checkoutDetails
                            )
                        }

                        item {
                            val selectedAddress =
                                viewModel.selectedAddress.collectAsStateWithLifecycle()
                            Spacer(Modifier.height(100.dp))
                            AddressCard(selectedAddress.value) {
                                viewModel.onAddressClicked()
                            }
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { viewModel.checkout() },
                                enabled = selectedAddress.value != null,
                                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                                shape = CircleShape,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 32.dp)
                            ) {
                                Text(
                                    text = "CHECKOUT",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(vertical = 12.dp)
                                )
                            }
                            Spacer(Modifier.height(16.dp))
                        }
                    }
                }
            }

            is CartViewModel.CartUiState.Loading -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.height(16.dp))
                    CircularProgressIndicator()
                }
            }

            else -> {
            }
        }
    }
}

@Composable
fun CartScreenHeader(onBackClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
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
        Text(text = "Cart", fontSize = 20.sp)
        Spacer(modifier = Modifier.size(32.dp))
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CartScreenBody(
    cartItem: CartItem,
    onItemCancelClick: () -> Unit,
    onIncreaseClick: () -> Unit,
    onDecreaseClick: () -> Unit
) {
    Spacer(Modifier.height(24.dp))
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(82.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Box(
            modifier = Modifier
                .size(82.dp)
                .background(color = Color.Red, shape = RoundedCornerShape(12.dp))
        ) {
            AsyncImage(
                model = cartItem.menuItemId.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(shape = RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = 4.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = cartItem.menuItemId.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontWeight = FontWeight.Bold, fontSize = 20.sp)
            )
            Text(
                text = cartItem.menuItemId.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = TextStyle(fontSize = 20.sp, color = Color.Gray)
            )
            Text(
                text = "$${cartItem.menuItemId.price}",
                style = TextStyle(fontSize = 20.sp, color = Orange)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cancel),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clickable(onClick = onItemCancelClick)
                    .align(Alignment.End)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecreaseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.White, CircleShape)
                        .border(1.dp, Orange, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_minus),
                        contentDescription = null,
                    )
                }
                Text(
                    text = String.format("%02d", cartItem.quantity),
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                IconButton(
                    onClick = onIncreaseClick,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Orange, CircleShape)
                        .clip(CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun CostDetail(name: String, price: Double, isTotal: Boolean = false, itemCount: Int = 0) {
    val formattedPrice = StringUtils.formatCurrency(price)
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = name, style = TextStyle(fontSize = 20.sp))
        if (isTotal) {
            val s = if (itemCount == 1) "" else "s"
            Text(
                text = " ($itemCount item$s)",
                style = TextStyle(fontSize = 20.sp, color = Color.Gray)
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            text = formattedPrice,
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
        )
        Spacer(Modifier.width(2.dp))
        Text(text = "USD", style = TextStyle(fontSize = 20.sp, color = Color.Gray))
    }
}

@Composable
fun CartCostSummary(itemCount: Int, checkoutDetails: CheckoutDetails) {
    Column {
        CostDetail(name = "Subtotal", price = checkoutDetails.subTotal)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        CostDetail(name = "Tax and Fees", price = checkoutDetails.tax)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        CostDetail(name = "Delivery", price = checkoutDetails.deliveryFee)
        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(16.dp))
        CostDetail(
            name = "Total",
            price = checkoutDetails.totalAmount,
            isTotal = true,
            itemCount = itemCount
        )
    }
}

@Composable
fun PromoCodeInput(
    promoCode: String,
    onPromoCodeChange: (String) -> Unit,
    onApplyClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Transparent, shape = RoundedCornerShape(60.dp))
            .border(
                width = 1.dp,
                color = Color.Gray.copy(alpha = 0.2f),
                shape = RoundedCornerShape(60.dp)
            )
            .padding(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = promoCode,
                onValueChange = onPromoCodeChange,
                placeholder = { Text(text = "Promo Code", color = Color.Gray) },
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.Transparent,
                    focusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            Button(
                onClick = { onApplyClick() },
                colors = ButtonDefaults.buttonColors(containerColor = Orange),
                shape = RoundedCornerShape(60),
                modifier = Modifier.height(60.dp)
            ) {
                Text(
                    text = "Apply",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    }
}

@Composable
fun AddressCard(address: Address?, onAddressClicked: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(2.dp)
            .clip(
                RoundedCornerShape(2.dp)
            )
            .background(Color.White)
            .clickable { onAddressClicked() }
            .padding(16.dp)

    ) {
        if (address != null) {
            Column {
                Text(text = address.addressLine1, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${address.city}, ${address.state}, ${address.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            Text(text = "Select Address", style = MaterialTheme.typography.bodyMedium)
        }
    }

}