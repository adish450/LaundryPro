package com.laundrypro.app.models

// Models the server's successful registration response
data class RegisterResponse(
    val message: String,
    val user: User
)