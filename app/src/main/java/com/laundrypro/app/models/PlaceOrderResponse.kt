package com.laundrypro.app.models

// This class matches the top-level structure of the successful order JSON response
data class PlaceOrderResponse(
    val message: String,
    val order: SimpleOrder
)