package com.dhobikart.app.models

import com.google.gson.annotations.SerializedName


// A simplified version of the cloth item for this specific response
data class SimpleClothOrderItem(
    @SerializedName("_id")
    val id: String,
    val clothId: String, // This is now a String
    val quantity: Int,
    val pricePerUnit: Double,
    val total: Double
)
