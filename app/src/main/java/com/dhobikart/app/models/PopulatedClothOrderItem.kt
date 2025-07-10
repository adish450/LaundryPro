package com.dhobikart.app.models

import com.google.gson.annotations.SerializedName

// Represents an item in the 'clothes' array where clothId is a full Cloth object
data class PopulatedClothOrderItem(
    @SerializedName("_id")
    val id: String,
    val clothId: Cloth, // This is now a full Cloth object
    val quantity: Int,
    val pricePerUnit: Double,
    val total: Double
)