package com.dhobikart.app.models

data class LaundryItem(
    val id: String,
    val name: String,
    val price: Double,
    val category: String = ""
)
