package com.laundrypro.app.models

data class LoginRequest(
    val email: String,
    val pass: String // The server expects 'pass' instead of 'password'
)