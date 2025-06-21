package com.laundrypro.app.repository

import com.laundrypro.app.data.RetrofitInstance
import com.laundrypro.app.models.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class LaundryRepository {

    private val apiService = RetrofitInstance.service
    // Mock data - In real app, this would come from API/Database
    private val mockServices = listOf(
        LaundryService(
            1, "Wash & Fold", "Regular washing and folding", "👕", 2.5,
            listOf(
                LaundryItem("shirt", "Shirt", 2.5),
                LaundryItem("tshirt", "T-Shirt", 2.0),
                LaundryItem("jeans", "Jeans", 3.0),
                LaundryItem("dress", "Dress", 4.0)
            )
        ),
        LaundryService(
            2, "Dry Cleaning", "Professional dry cleaning", "🧥", 8.0,
            listOf(
                LaundryItem("suit", "Suit", 15.0),
                LaundryItem("coat", "Coat", 12.0),
                LaundryItem("blazer", "Blazer", 10.0)
            )
        ),
        LaundryService(
            3, "Wash & Iron", "Washing with professional ironing", "👔", 4.0,
            listOf(
                LaundryItem("shirt", "Shirt", 4.0),
                LaundryItem("pants", "Pants", 4.5),
                LaundryItem("dress", "Dress", 6.0)
            )
        ),
        LaundryService(
            4, "Express Service", "Same day service", "⚡", 6.0,
            listOf(
                LaundryItem("any", "Any Item", 6.0)
            )
        )
    )

    private val mockOffers = listOf(
        Offer("1", "50% OFF First Order", "New customers get 50% off", "FIRST50", 50.0, "2024-12-31"),
        Offer("2", "Express Service Free", "Same day delivery at no extra cost", "EXPRESS24", 0.0, "2024-11-30"),
        Offer("3", "Weekend Special", "20% off weekend orders", "WEEKEND20", 20.0, "2024-12-31")
    )

    suspend fun getServices(): List<LaundryService> {
        delay(500) // Simulate network delay
        return mockServices
    }

    suspend fun getOffers(): List<Offer> {
        delay(300)
        return mockOffers
    }

    suspend fun login(email: String, password: String): User {
        val request = LoginRequest(email, password)
        val response = apiService?.login(request)

        if (response?.isSuccessful == true) {
            return response.body()?.user ?: throw Exception("User data not found in login response")
        } else {
            throw Exception("Login failed: ${response?.message()}")
        }
    }

    suspend fun register(name: String, email: String, password: String, phone: String): User {
        val request = RegisterRequest(name, email, phone, password)
        val response = apiService?.register(request)

        if (response?.isSuccessful == true) {
            return response.body()?.user ?: throw Exception("User data not found in register response")
        } else {
            throw Exception("Registration failed: ${response?.message()}")
        }
    }

    suspend fun createOrder(orderData: OrderData): Order {
        delay(1000)

        val orderId = "ORD${System.currentTimeMillis()}"
        val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        return Order(
            id = orderId,
            userId = orderData.userId,
            items = orderData.items,
            totalAmount = orderData.totalAmount,
            pickupAddress = orderData.pickupAddress,
            pickupDateTime = orderData.pickupDateTime,
            status = OrderStatus.SCHEDULED,
            createdAt = currentTime,
            updatedAt = currentTime
        )
    }

    suspend fun getUserOrders(userId: String): List<Order> {
        delay(500)
        // Return mock orders for the user
        return emptyList() // In real app, fetch from database
    }
}