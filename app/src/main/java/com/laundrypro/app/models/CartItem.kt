package com.laundrypro.app.models

data class CartItem(
    val itemId: String,
    val name: String,
    val price: Double,
    val quantity: Int,
    val serviceId: String // Ensure this is a String
)