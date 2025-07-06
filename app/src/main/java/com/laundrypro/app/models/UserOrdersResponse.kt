package com.laundrypro.app.models

// This class matches the structure: { "orders": [...], "message": "..." }
data class UserOrdersResponse(
    val orders: List<Order>,
    val message: String
)