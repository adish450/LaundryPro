package com.dhobikart.app.models

// This class represents the entire JSON response from the new endpoint
data class ServiceWithClothesResponse(
    val serviceId: String,
    val serviceName: String,
    val clothes: List<ServiceCloth>
)
