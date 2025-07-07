package com.laundrypro.app.models

// This class will match the response when fetching all orders
data class AllOrdersResponse(
    val orders: List<AdminOrder>,
    val message: String
)