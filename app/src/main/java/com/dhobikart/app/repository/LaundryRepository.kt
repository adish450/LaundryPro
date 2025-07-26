package com.dhobikart.app.repository

import android.util.Patterns
import com.dhobikart.app.data.RetrofitInstance
import com.dhobikart.app.models.*
import com.dhobikart.app.models.Address
import com.google.gson.Gson
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
        Offer(
            "1",
            "50% OFF First Order",
            "New customers get 50% off",
            "FIRST50",
            50.0,
            "2025-12-31"
        ),
        Offer(
            "2",
            "Express Service Free",
            "Same day delivery at no extra cost",
            "EXPRESS24",
            0.0,
            "2025-12-31"
        ),
        Offer(
            "3",
            "Weekend Special",
            "20% off weekend orders",
            "WEEKEND20",
            20.0,
            "2025-12-31")
    )

    /*suspend fun getServices(): List<LaundryService> {
        delay(500) // Simulate network delay
        return mockServices
    }*/

    suspend fun getServices(): List<Service> {
        val response = apiService.getServices()
        if (response.isSuccessful) {
            val responseBody = response.body() ?: throw Exception("Empty response body for services")
            // **THE FIX:** Manually parse the JSON string from the response body.
            val jsonString = responseBody.string()
            val serviceResponse = Gson().fromJson(jsonString, ServiceResponse::class.java)
            return serviceResponse.data
        } else {
            throw Exception("Failed to fetch services: ${response.errorBody()?.string()}")
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
            // **THE FIX:** Parse the error body to get the clean message.
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                // Throw an exception with the server's specific error message.
                throw Exception(errorResponse.error)
            }
            // Fallback for other types of errors.
            throw Exception("Login failed with status code: ${response.code()}")
        }
    }

    suspend fun register(
        name: String,
        email: String,
        password: String,
        phone: String
    ): RegisterResponse {
        // --- START: Input Validation ---

        // 1. Validate Email Format
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            throw Exception("Please enter a valid email address.")
        }

        // 2. Validate 10-Digit Indian Phone Number
        val indianPhoneRegex = Regex("^[6-9]\\d{9}$")
        if (!phone.matches(indianPhoneRegex)) {
            throw Exception("Please enter a valid 10-digit Indian mobile number.")
        }

        // 3. Validate Strong Password
        // (At least 6 characters, 1 letter, 1 number, 1 special character)
        val passwordRegex =
            Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*#?&])[A-Za-z\\d@\$!%*#?&]{6,}\$")
        if (!password.matches(passwordRegex)) {
            throw Exception("Password must be at least 6 characters long and include a letter, a number, and a special character.")
        }
        // --- END: Input Validation ---

        // Prepend country code after validation
        val formattedPhone = "+91$phone"

        val request = RegisterRequest(name, email, formattedPhone, password)
        val response = apiService.register(request)

        if (response.isSuccessful) {
            return response.body() ?: throw Exception("Empty response body for registration")
        } else {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                // Check for the specific MongoDB duplicate key error.
                if (errorResponse.error.contains("E11000 duplicate key error")) {
                    throw Exception("This email is already registered. Please log in or use a different email.")
                }
                // If it's another known error, throw its message.
                throw Exception(errorResponse.error)
            }
            // Fallback for unknown errors.
            throw Exception("Registration failed with status code: ${response.code()}")
        }
    }

    /**
     * Sends a request to the server to email a password reset OTP to the user.
     */
    suspend fun requestOtp(email: String) {
        val request = ForgotPasswordRequest(email)
        val response = apiService.requestOtp(request)
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                // **THE FIX:** Parse the error body to get the clean message.
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                throw Exception(errorResponse.error)
            }
            throw Exception("Failed to request OTP with status code: ${response.code()}")
        }
    }

    /**
     * Sends a request to the server to reset the user's password using the provided OTP.
     */
    suspend fun resetPassword(email: String, otp: String, newPassword: String) {
        val request = ResetPasswordRequest(email, otp, newPassword)
        val response = apiService.resetPassword(request)
        if (!response.isSuccessful) {
            val errorBody = response.errorBody()?.string()
            if (errorBody != null) {
                val errorResponse = Gson().fromJson(errorBody, ErrorResponse::class.java)
                throw Exception(errorResponse.error)
            }
            throw Exception("Failed to reset password with status code: ${response.code()}")
        }
    }

    suspend fun placeOrder(
        user: User,
        cartItems: List<CartItem>,
        totalAmount: Double,
        pickupAddress: Address
    ): SimpleOrder {
        val serviceOrders = cartItems.groupBy { it.serviceId }
            .map { (serviceId, items) ->
                val clothOrderItems = items.map { cartItem ->
                    ClothOrderItem(
                        id = cartItem.itemId,
                        clothId = cartItem.itemId,
                        quantity = cartItem.quantity,
                        pricePerUnit = cartItem.price,
                        total = cartItem.price * cartItem.quantity
                    )
                }
                ServiceOrderItem(serviceId, clothOrderItems)
            }

        val request = PlaceOrderRequest(
            userId = user.id ?: throw Exception("User ID not found"),
            services = serviceOrders,
            totalAmount = totalAmount,
            paymentMode = "Online",
            paymentStatus = "Pending",
            pickupAddress = pickupAddress,
            status = "Pending"
        )

        val response = apiService.placeOrder(request)
        if (response.isSuccessful) {
            // The response body is now the SimpleOrder object itself.
            return response.body() ?: throw Exception("Order data not found in response")
        } else {
            throw Exception("Failed to place order: ${response.errorBody()?.string()}")
        }
    }


    suspend fun getUserOrders(userId: String): List<Order> {
        val response = apiService.getUserOrders(userId)
        if (response.isSuccessful) {
            return response.body()?.orders ?: emptyList()
        } else {
            throw Exception("Failed to fetch orders: ${response.errorBody()?.string()}")
        }
    }

    suspend fun getAllOrders(): List<AdminOrder> {
        val response = apiService.getAllOrders()
        if (response.isSuccessful) {
            return response.body()?.orders ?: emptyList()
        } else {
            throw Exception("Failed to fetch all orders: ${response.errorBody()?.string()}")
        }
    }

    suspend fun updateUserProfile(
        name: String,
        email: String,
        phone: String,
        addresses: List<Address>
    ): User {
        val request = UpdateProfileRequest(name, email, phone, addresses)
        val response = apiService.updateUserProfile(request)
        if (response.isSuccessful) {
            // **THE FIX:** Extract the nested 'user' object from the response body.
            return response.body()?.user ?: throw Exception("User data not found in response")
        } else {
            throw Exception("Failed to update profile: ${response.errorBody()?.string()}")
        }
    }
}