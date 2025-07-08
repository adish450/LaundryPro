package com.laundrypro.app.data

import com.laundrypro.app.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<Unit>


    @GET("api/service/allservices")
    suspend fun getServices(): Response<ServiceResponse>

    // ADD the new, more efficient endpoint:
    @GET("api/service/clothes/{serviceId}")
    suspend fun getServiceWithClothes(@Path("serviceId") serviceId: String
    ): Response<ServiceWithClothesResponse>

    @POST("api/order/")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<PlaceOrderResponse>

    @GET("api/order/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: String): Response<UserOrdersResponse>

    // NEW: Endpoint for admins to get all orders
    @GET("api/order/all")
    suspend fun getAllOrders(): Response<AllOrdersResponse>

}