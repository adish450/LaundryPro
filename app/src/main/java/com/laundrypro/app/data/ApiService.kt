package com.laundrypro.app.data

import com.laundrypro.app.models.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("/api/order/")
    suspend fun placeOrder(
        @Header("Authorization") token: String,
        @Body request: PlaceOrderRequest
    ): Response<Order>

    @GET("/api/order/user/{userId}")
    suspend fun getUserOrders(
        @Header("Authorization") token: String,
        @Path("userId") userId: String
    ): Response<List<Order>>
}