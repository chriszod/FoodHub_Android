package com.example.foodhub_android.data

import com.example.foodhub_android.data.models.AddToCartRequest
import com.example.foodhub_android.data.models.AddToCartResponse
import com.example.foodhub_android.data.models.Address
import com.example.foodhub_android.data.models.AddressListResponse
import com.example.foodhub_android.data.models.AuthResponse
import com.example.foodhub_android.data.models.CartResponse
import com.example.foodhub_android.data.models.CategoriesResponse
import com.example.foodhub_android.data.models.ConfirmPaymentRequest
import com.example.foodhub_android.data.models.ConfirmPaymentResponse
import com.example.foodhub_android.data.models.FCMRequest
import com.example.foodhub_android.data.models.FoodItem
import com.example.foodhub_android.data.models.FoodItemListResponse
import com.example.foodhub_android.data.models.FoodItemResponse
import com.example.foodhub_android.data.models.GenericMsgResponse
import com.example.foodhub_android.data.models.ImageUploadResponse
import com.example.foodhub_android.data.models.LoginRequest
import com.example.foodhub_android.data.models.NotificationListResponse
import com.example.foodhub_android.data.models.OAuthRequest
import com.example.foodhub_android.data.models.Order
import com.example.foodhub_android.data.models.OrderListResponse
import com.example.foodhub_android.data.models.PaymentIntentRequest
import com.example.foodhub_android.data.models.PaymentIntentResponse
import com.example.foodhub_android.data.models.Restaurant
import com.example.foodhub_android.data.models.RestaurantsResponse
import com.example.foodhub_android.data.models.ReverseGeoCodeRequest
import com.example.foodhub_android.data.models.SignUpRequest
import com.example.foodhub_android.data.models.UpdateCartItemRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface FoodApi {
    @POST("/auth/signup")
    suspend fun signUp(@Body request: SignUpRequest): Response<AuthResponse>

    @POST("/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("/auth/oauth")
    suspend fun oAuth(@Body request: OAuthRequest): Response<AuthResponse>

    @GET("/categories")
    suspend fun getCategories(): Response<CategoriesResponse>

    @GET("/restaurants")
    suspend fun getRestaurants(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<RestaurantsResponse>

    @GET("/restaurants/{restaurantId}/menu")
    suspend fun getFoodItemForRestaurant(
        @Path("restaurantId") restaurantId: String
    ): Response<FoodItemResponse>

    @POST("/cart")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<AddToCartResponse>

    @GET("/cart")
    suspend fun getCart(): Response<CartResponse>

    @PATCH("/cart")
    suspend fun updateCart(@Body request: UpdateCartItemRequest): Response<GenericMsgResponse>

    @DELETE("/cart/{cartItemId}")
    suspend fun deleteCartItem(@Path("cartItemId") cartItemId: String): Response<GenericMsgResponse>

    @GET("/addresses")
    suspend fun getUserAddress(): Response<AddressListResponse>

    @POST("/addresses/reverse-geocode")
    suspend fun reverseGeocode(@Body request: ReverseGeoCodeRequest): Response<Address>

    @POST("/addresses")
    suspend fun storeAddress(@Body address: Address): Response<GenericMsgResponse>

    @POST("/payments/create-intent")
    suspend fun getPaymentIntent(@Body request: PaymentIntentRequest): Response<PaymentIntentResponse>

    @POST("/payments/confirm/{paymentIntentId}")
    suspend fun verifyPurchase(
        @Body request: ConfirmPaymentRequest, @Path("paymentIntentId") paymentIntentId: String
    ): Response<ConfirmPaymentResponse>

    @GET("/orders")
    suspend fun getOrders(): Response<OrderListResponse>

    @GET("/orders/{orderId}")
    suspend fun getOrderDetails(@Path("orderId") orderId: String): Response<Order>

    @PUT("/notifications/fcm-token")
    suspend fun updateToken(@Body request: FCMRequest): Response<GenericMsgResponse>

    @POST("/notifications/{id}/read")
    suspend fun readNotification(@Path("id") id: String): Response<GenericMsgResponse>

    @GET("/notifications")
    suspend fun getNotifications(): Response<NotificationListResponse>

    @GET("/restaurant-owner/profile")
    suspend fun getRestaurantProfile(): Response<Restaurant>

    @GET("/restaurant-owner/orders")
    suspend fun getRestaurantOrders(@Query("status") status: String): Response<OrderListResponse>

    @PATCH("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body map: Map<String, String>
    ): Response<GenericMsgResponse>

    @GET("/restaurants/{id}/menu")
    suspend fun getRestaurantMenu(@Path("id") restaurantId: String): Response<FoodItemListResponse>

    @POST("/restaurants/{id}/menu")
    suspend fun addRestaurantMenu(
        @Path("id") restaurantId: String,
        @Body foodItem: FoodItem
    ): Response<GenericMsgResponse>

    @POST("/images/upload")
    @Multipart
    suspend fun uploadImage(@Part image: MultipartBody.Part): Response<ImageUploadResponse>

//    @GET("/rider/deliveries/available")
//    suspend fun getAvailableDeliveries(): Response<DelieveriesListResponse>

//    @POST("/rider/deliveries/{orderId}/reject")
//    suspend fun rejectDelivery(@Path("orderId") orderId: String): Response<GenericMsgResponse>

//    @POST("/rider/deliveries/{orderId}/accept")
//    suspend fun acceptDelivery(@Path("orderId") orderId: String): Response<GenericMsgResponse>
//
//    @GET("/rider/deliveries/active")
//    suspend fun getActiveDeliveries(): Response<RiderDeliveryOrderListResponse>
}