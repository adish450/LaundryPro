package com.dhobikart.app.models

// Represents a service and the list of cart items belonging to it
data class GroupedCartItems(
    val serviceName: String,
    val items: List<CartItem>
)