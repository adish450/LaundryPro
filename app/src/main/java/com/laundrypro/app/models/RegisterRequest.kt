package com.laundrypro.app.models

data class RegisterRequest(
    val name: String,
    val email: String,
    val pass: String,
    val phone: String
)