package com.dhobikart.app.models

import com.google.gson.annotations.SerializedName

// Represents a single item within the 'clothes' array of an order
data class ClothOrderItem(
    @SerializedName("_id")
    val id: String, // Added to match the response
    val clothId: String,
    val quantity: Int,
    val pricePerUnit: Double,
    val total: Double
)