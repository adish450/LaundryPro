package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    @SerializedName(value="zip", alternate=["zipCode"]) // Handles both 'zip' and 'zipCode'
    val zip: String?
)