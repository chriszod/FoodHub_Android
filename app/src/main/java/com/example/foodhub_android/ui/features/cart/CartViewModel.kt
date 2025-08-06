package com.example.foodhub_android.ui.features.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.foodhub_android.data.FoodApi
import com.example.foodhub_android.data.models.Address
import com.example.foodhub_android.data.models.CartItem
import com.example.foodhub_android.data.models.CartResponse
import com.example.foodhub_android.data.models.ConfirmPaymentRequest
import com.example.foodhub_android.data.models.PaymentIntentRequest
import com.example.foodhub_android.data.models.PaymentIntentResponse
import com.example.foodhub_android.data.models.UpdateCartItemRequest
import com.example.foodhub_android.data.remote.ApiResponse
import com.example.foodhub_android.data.remote.safeApiCall
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(val foodApi: FoodApi) : ViewModel() {
    private val _uiState = MutableStateFlow<CartUiState>(CartUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _event = MutableSharedFlow<CartNavigationEvent>()
    val event = _event.asSharedFlow()

    private val _cartItemCount = MutableStateFlow(0)
    val cartItemCount = _cartItemCount.asStateFlow()

    private var cartResponse: CartResponse? = null

    private val address = MutableStateFlow<Address?>(null)
    val selectedAddress = address.asStateFlow()

    private var paymentIntent: PaymentIntentResponse? = null

    init {
        getCart()
    }

    fun getCart() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            val response = safeApiCall { foodApi.getCart() }
            when (response) {
                is ApiResponse.Success -> {
                    cartResponse = response.data
                    _uiState.value = CartUiState.Success(response.data)
                    _cartItemCount.value = response.data.items.size
                }

                is ApiResponse.Error -> {
                    _uiState.value = CartUiState.Error(response.message)
                }

                else -> {
                    _uiState.value = CartUiState.Error("Unknown error")
                }
            }
        }
    }

    private fun updateItemQuantity(cartItem: CartItem, quantity: Int) {
        _uiState.value = CartUiState.Loading
        viewModelScope.launch {
            val response =
                safeApiCall { foodApi.updateCart(UpdateCartItemRequest(cartItem.id, quantity)) }

            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    _event.emit(CartNavigationEvent.OnQuantityUpdateError)
                }
            }
        }
    }

    fun increaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity < 20) {
            updateItemQuantity(cartItem, cartItem.quantity + 1)
        }
    }

    fun decreaseQuantity(cartItem: CartItem) {
        if (cartItem.quantity > 1) {
            updateItemQuantity(cartItem, cartItem.quantity - 1)
        }
    }

    fun removeItem(cartItem: CartItem) {
        _uiState.value = CartUiState.Loading
        viewModelScope.launch {
            val response =
                safeApiCall { foodApi.deleteCartItem(cartItem.id) }
            when (response) {
                is ApiResponse.Success -> {
                    getCart()
                }

                else -> {
                    cartResponse?.let {
                        _uiState.value = CartUiState.Success(cartResponse!!)
                    }
                    _event.emit(CartNavigationEvent.OnItemRemoveError)
                }
            }
        }
    }

    fun applyPromo() {

    }

    fun checkout() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            val paymentDetails =
                safeApiCall { foodApi.getPaymentIntent(PaymentIntentRequest(addressId = address.value!!.id!!)) }
            when (paymentDetails) {
                is ApiResponse.Success -> {
                    paymentIntent = paymentDetails.data
                    _event.emit(CartNavigationEvent.OnInitiatePayment(paymentDetails.data))
                    _uiState.value = CartUiState.Success(cartResponse!!)
                }

                else -> {
                    _uiState.value = CartUiState.Success(cartResponse!!)
                }
            }
        }
    }

    fun onAddressClicked() {
        viewModelScope.launch {
            _event.emit(CartNavigationEvent.OnAddressClicked)
        }
    }

    fun onAddressSelected(it: Address) {
        address.value = it
    }

    fun onPaymentSuccess() {
        viewModelScope.launch {
            _uiState.value = CartUiState.Loading
            val response = safeApiCall {
                foodApi.verifyPurchase(
                    ConfirmPaymentRequest(paymentIntent!!.paymentIntentId, address.value!!.id!!),
                    paymentIntent!!.paymentIntentId
                )
            }
            when (response) {
                is ApiResponse.Success -> {
                    _event.emit(CartNavigationEvent.OrderSuccess(response.data.orderId))
                    _uiState.value = CartUiState.Success(cartResponse!!)
                    getCart()
                }

                else -> {
                    _event.emit(CartNavigationEvent.ShowErrorDialog)
                    _uiState.value = CartUiState.Success(cartResponse!!)
                }
            }
        }
    }

    fun onPaymentFailure() {
        viewModelScope.launch {
            _event.emit(CartNavigationEvent.ShowErrorDialog)
        }
    }

    sealed class CartUiState {
        object Idle : CartUiState()
        object Loading : CartUiState()
        data class Success(val data: CartResponse) : CartUiState()
        data class Error(val message: String) : CartUiState()
    }

    sealed class CartNavigationEvent {
        data class OnInitiatePayment(val data: PaymentIntentResponse) : CartNavigationEvent()
        object OnCheckout : CartNavigationEvent()
        object OnQuantityUpdateError : CartNavigationEvent()
        object OnItemRemoveError : CartNavigationEvent()
        object OnPromoApplyError : CartNavigationEvent()
        object OnAddressClicked : CartNavigationEvent()
        object ShowErrorDialog : CartNavigationEvent()
        data class OrderSuccess(val orderId: String?) : CartNavigationEvent()
    }
}