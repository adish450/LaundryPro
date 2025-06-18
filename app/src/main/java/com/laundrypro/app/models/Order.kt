package com.laundrypro.app.models

data class Order(
    val id: String,
    val userId: String,
    val items: List<CartItem>,
    val totalAmount: Double,
    val pickupAddress: String,
    val pickupDateTime: String,
    val status: OrderStatus,
    val createdAt: String,
    val updatedAt: String
)
