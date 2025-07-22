package com.dhobikart.app.models

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)