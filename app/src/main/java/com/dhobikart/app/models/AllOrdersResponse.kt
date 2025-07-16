package com.laundrypro.app.models

import com.dhobikart.app.models.AdminOrder

// This class will match the response when fetching all orders
data class AllOrdersResponse(
    val orders: List<AdminOrder>,
    val message: String
)