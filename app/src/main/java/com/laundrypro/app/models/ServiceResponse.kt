package com.laundrypro.app.models

// This class matches the top-level structure of the JSON response
data class ServiceResponse(
    val data: List<Service>,
    val message: String
)