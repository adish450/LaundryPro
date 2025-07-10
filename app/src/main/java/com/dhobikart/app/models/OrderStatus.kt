package com.dhobikart.app.models

enum class OrderStatus(val displayName: String) {
    SCHEDULED("Pickup Scheduled"),
    PICKED_UP("Picked Up"),
    IN_PROCESSING("In Processing"),
    READY_FOR_DELIVERY("Ready for Delivery"),
    OUT_FOR_DELIVERY("Out for Delivery"),
    DELIVERED("Delivered"),
    CANCELLED("Cancelled")
}