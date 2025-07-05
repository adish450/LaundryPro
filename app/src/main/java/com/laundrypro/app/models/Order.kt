package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName


data class Order(
    @SerializedName("_id")
    val id: String,
    val userId: String,
    val serviceId: Service, // This is now a full Service object
    val clothes: List<ClothOrderItem>,
    val totalAmount: Double,
    val status: String,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address?, // Made nullable to prevent parsing errors
    val dropAddress: Address?,   // Made nullable
    val createdAt: String,
    val updatedAt: String
)
