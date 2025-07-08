package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// This model matches the response from the POST /api/order/ endpoint
data class SimpleOrder(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val services: List<SimpleServiceOrderItem>, // This now matches the new schema
    val totalAmount: Double,
    val status: String,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address?,
    val dropAddress: Address?,
    val createdAt: String,
    val updatedAt: String
)