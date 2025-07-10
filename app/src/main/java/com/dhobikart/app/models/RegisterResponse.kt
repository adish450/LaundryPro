package com.dhobikart.app.models

// Models the server's successful registration response
data class RegisterResponse(
    val token: String,
    val user: User
)