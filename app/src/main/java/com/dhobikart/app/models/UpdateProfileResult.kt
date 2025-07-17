package com.dhobikart.app.models

sealed class UpdateProfileResult {
    data object Idle : UpdateProfileResult()
    data object Loading : UpdateProfileResult()
    data class Success(val user: User) : UpdateProfileResult()
    data class Error(val message: String) : UpdateProfileResult()
}