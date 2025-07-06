package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// This model matches the response from the POST /api/order/ endpoint
data class SimpleOrder(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val serviceId: String, // This is now a String
    val clothes: List<SimpleClothOrderItem>, // This uses a simplified item model
    val totalAmount: Double,
    val status: String,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address?,
    val dropAddress: Address?,
    val createdAt: String,
    val updatedAt: String
)