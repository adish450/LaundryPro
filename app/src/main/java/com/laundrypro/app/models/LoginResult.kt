package com.laundrypro.app.models

sealed class LoginResult {
    data object Loading : LoginResult()
    data class Success(val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data object Idle : LoginResult() // An initial state
}