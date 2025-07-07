package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// This model represents an order with the user's details populated
data class AdminOrder(
    @SerializedName("_id")
    val id: String,
    val userId: User, // This is now a full User object
    val serviceId: Service,
    val clothes: List<PopulatedClothOrderItem>,
    val totalAmount: Double,
    val status: String,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address?,
    val dropAddress: Address?,
    val createdAt: String,
    val updatedAt: String
)