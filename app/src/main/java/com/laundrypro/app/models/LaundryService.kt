package com.laundrypro.app.models

data class LaundryService(
    val id: Int,
    val name: String,
    val description: String,
    val icon: String,
    val basePrice: Double,
    val items: List<LaundryItem>
)
