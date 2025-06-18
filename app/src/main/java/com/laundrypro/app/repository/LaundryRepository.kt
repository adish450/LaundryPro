package com.laundrypro.app.repository

import com.laundrypro.app.models.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

class LaundryRepository {

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
        delay(1000) // Simulate network call

        // Mock validation
        if (email.isNotEmpty() && password.isNotEmpty()) {
            return User(
                id = "user_${System.currentTimeMillis()}",
                name = "John Doe",
                email = email,
                phone = "+1234567890",
                addresses = listOf(
                    Address("addr1", "Home", "123 Main St, City, State 12345", true)
                )
            )
        } else {
            throw Exception("Invalid credentials")
        }
    }

    suspend fun register(userData: Map<String, String>): User {
        delay(1000)

        val name = userData["name"] ?: throw Exception("Name is required")
        val email = userData["email"] ?: throw Exception("Email is required")
        val phone = userData["phone"] ?: throw Exception("Phone is required")

        return User(
            id = "user_${System.currentTimeMillis()}",
            name = name,
            email = email,
            phone = phone
        )
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