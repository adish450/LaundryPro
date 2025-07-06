package com.laundrypro.app.models

// This model represents a single cloth item within the new API response
data class ServiceCloth(
    val clothId: String,
    val name: String,
    val imageUrl: String?,
    val price: Double
)