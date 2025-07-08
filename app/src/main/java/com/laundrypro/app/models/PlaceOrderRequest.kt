package com.laundrypro.app.models

// This model defines the exact JSON body to send when placing an order
data class PlaceOrderRequest(
    val userId: String,
    val services: List<ServiceOrderItem>, // Changed to use the new request-specific model
    val totalAmount: Double,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address,
    val status: String
)