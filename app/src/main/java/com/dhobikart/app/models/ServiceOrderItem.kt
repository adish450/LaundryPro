package com.dhobikart.app.models

/**
 * Represents a service within the request body when placing a new order.
 * This uses simple String IDs for services and clothes.
 */
data class ServiceOrderItem(
    val serviceId: String,
    val clothes: List<ClothOrderItem>
)