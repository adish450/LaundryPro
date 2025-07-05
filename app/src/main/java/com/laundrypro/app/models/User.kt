package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// The User model is updated to correctly map the "_id" field from the server.
data class User(
    @SerializedName("_id")
    val id: String?,
    val name: String?,
    val email: String?,
    val phone: String?,
    val address: Address?
)