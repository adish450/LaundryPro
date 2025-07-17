package com.dhobikart.app.data

import com.dhobikart.app.models.*
import com.laundrypro.app.models.AllOrdersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @GET("api/service/allservices")
    suspend fun getServices(): Response<ServiceResponse>

    @GET("api/service/clothes/{serviceId}")
    suspend fun getServiceWithClothes(@Path("serviceId") serviceId: String): Response<ServiceWithClothesResponse>

    @POST("api/order/")
    suspend fun placeOrder(@Body request: PlaceOrderRequest): Response<SimpleOrder>

    @GET("api/order/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: String): Response<UserOrdersResponse>

    @GET("api/order/getAllOrders")
    suspend fun getAllOrders(): Response<AllOrdersResponse>

    @POST("api/profile/updateprofile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<User>

}