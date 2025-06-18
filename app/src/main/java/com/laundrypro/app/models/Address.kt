package com.laundrypro.app.models

data class Address(
    val id: String,
    val label: String,
    val fullAddress: String,
    val isDefault: Boolean = false
)
