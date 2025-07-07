package com.laundrypro.app.repository

import android.util.Log
import com.laundrypro.app.data.RetrofitInstance
import com.laundrypro.app.models.*
import kotlinx.coroutines.delay

class LaundryRepository {

    private val apiService = RetrofitInstance.api
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

    /*suspend fun getServices(): List<LaundryService> {
        delay(500) // Simulate network delay
        return mockServices
    }*/

    suspend fun getServices(): List<Service> {
        val response = apiService.getServices()
        if (response.isSuccessful) {
            // This is the fix: Extract the list from the 'data' field of the response body
            return response.body()?.data ?: emptyList()
        } else {
            throw Exception("Failed to fetch services")
        }
    }

    suspend fun getServiceWithClothes(serviceId: String): List<ServiceCloth> {
        val response = apiService.getServiceWithClothes(serviceId)
        if (response.isSuccessful) {
            // Extract the list of clothes from the wrapper object
            return response.body()?.clothes ?: emptyList()
        } else {
            throw Exception("Failed to fetch clothes for service $serviceId")
        }
    }


    suspend fun getOffers(): List<Offer> {
        delay(300)
        return mockOffers
    }

    suspend fun login(email: String, password: String): LoginResponse {
        val request = LoginRequest(email, password)
        val response = apiService.login(request)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception(errorBody ?: "Login failed with status code: ${response.code()}")

        }
    }

    suspend fun register(name: String, email: String, password: String, phone: String): RegisterResponse {
        val request = RegisterRequest(name, email, password, phone)
        val response = apiService.register(request)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body")
        } else {
            val errorBody = response.errorBody()?.string()
            throw Exception(errorBody ?: "Registration failed with status code: ${response.code()}")
        }
    }

    suspend fun placeOrder(
        user: User,
        cartItems: List<CartItem>,
        totalAmount: Double,
        pickupAddress: Address
    ): SimpleOrder {
        val serviceId = cartItems.firstOrNull()?.serviceId ?: throw Exception("Cart is empty")

        val clothOrderItems = cartItems.map { cartItem ->
            ClothOrderItem(
                id = null.toString(),
                clothId = cartItem.itemId,
                quantity = cartItem.quantity,
                pricePerUnit = cartItem.price,
                total = cartItem.price * cartItem.quantity
            )
        }

        val request = PlaceOrderRequest(
            userId = user.id ?: throw Exception("User ID not found"),
            serviceId = serviceId,
            clothes = clothOrderItems,
            totalAmount = totalAmount,
            paymentMode = "Online",
            paymentStatus = "Pending",
            pickupAddress = pickupAddress,
            status = "Pending"
        )

        // Pass the formatted token to the API service
        val response = apiService.placeOrder(request)
        Log.d("LaundryRepository","response.isSuccessful")
        if (response.isSuccessful) {
            return response.body()?.order ?: throw Exception("Order data not found in response")
        } else {
            throw Exception("Failed to place order: ${response.errorBody()?.string()}")
        }
    }


    suspend fun getUserOrders(userId: String): List<Order> {
        val response = apiService.getUserOrders(userId)
        if (response.isSuccessful) {
            // This is the fix: Extract the list from the 'orders' field of the response body
            return response.body()?.orders ?: emptyList()
        } else {
            throw Exception("Failed to fetch orders: ${response.errorBody()?.string()}")
        }
    }

    suspend fun getAllOrders(token: String): List<AdminOrder> {
        val response = apiService.getAllOrders()
        if (response.isSuccessful) {
            return response.body()?.orders ?: emptyList()
        } else {
            throw Exception("Failed to fetch all orders: ${response.errorBody()?.string()}")
        }
    }
}