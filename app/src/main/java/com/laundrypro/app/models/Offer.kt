package com.laundrypro.app.models

data class Offer(
    val id: String,
    val title: String,
    val description: String,
    val code: String,
    val discountPercentage: Double,
    val validUntil: String,
    val isActive: Boolean = true
)
