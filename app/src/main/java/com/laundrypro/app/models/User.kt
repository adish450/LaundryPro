package com.laundrypro.app.models

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val addresses: List<Address> = emptyList()
)