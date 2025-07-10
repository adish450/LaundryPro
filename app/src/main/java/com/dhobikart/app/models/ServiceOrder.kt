package com.dhobikart.app.models

// Represents a single service and its clothes in a PlaceOrderRequest
data class ServiceOrder(
    val serviceId: Service,
    val clothes: List<PopulatedClothOrderItem>
)