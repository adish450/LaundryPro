package com.dhobikart.app.models

data class OrderData(
    val userId: String,
    val items: List<CartItem>,
    val pickupAddress: String,
    val pickupDateTime: String,
    val appliedOffer: Offer?,
    val totalAmount: Double
)
