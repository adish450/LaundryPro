package com.laundrypro.app.models

// Models the server's successful login response
data class LoginResponse(
    val token: String,
    val user: User
)