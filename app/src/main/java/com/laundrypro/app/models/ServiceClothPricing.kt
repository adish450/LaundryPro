package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// Represents the pricing link between a service and a cloth item
data class ServiceClothPricing(
    @SerializedName("_id")
    val id: String,
    val serviceId: String,
    val clothId: String,
    val price: Double
)