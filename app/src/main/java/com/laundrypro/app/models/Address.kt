package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    @SerializedName(value="zip", alternate=["zipCode"])
    val zip: String?
) {
    // This helper function creates a readable address string for the UI
    fun toDisplayString(): String {
        return "$street, $city, $state - $zip"
    }
}