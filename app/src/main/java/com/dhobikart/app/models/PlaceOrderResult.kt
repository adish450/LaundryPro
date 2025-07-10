package com.dhobikart.app.models

// This sealed class represents the different states of the place order operation
sealed class PlaceOrderResult {
    data object Idle : PlaceOrderResult() // Represents the initial state
    data object Loading : PlaceOrderResult()
    data class Success(val order: SimpleOrder) : PlaceOrderResult()
    data class Error(val message: String) : PlaceOrderResult()
}