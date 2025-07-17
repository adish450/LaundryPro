package com.dhobikart.app.models

data class UpdateProfileRequest(
    val name: String,
    val email: String,
    val phone: String,
    val address: List<Address>
)