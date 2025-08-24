package com.dhobikart.app.models

sealed class ForgotPasswordResult {
    data object OtpSent : ForgotPasswordResult()
    data object Success : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
    data object Idle : ForgotPasswordResult()
}