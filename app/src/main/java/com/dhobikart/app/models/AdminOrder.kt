package com.dhobikart.app.models

import com.google.gson.annotations.SerializedName

data class AdminOrder(
    @SerializedName("_id")
    val id: String,
    val userId: User,
    val services: List<ServiceOrder>,
    val totalAmount: Double,
    val status: String,
    val paymentMode: String,
    val paymentStatus: String,
    val pickupAddress: Address?,
    val dropAddress: Address?,
    val createdAt: String,
    val updatedAt: String
)