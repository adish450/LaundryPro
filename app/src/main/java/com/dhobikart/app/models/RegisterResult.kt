package com.dhobikart.app.models

sealed class RegisterResult {
    data object Loading : RegisterResult()
    data class Success(val user: User) : RegisterResult()
    data class Error(val message: String) : RegisterResult()
    data object Idle : RegisterResult() // An initial state
}