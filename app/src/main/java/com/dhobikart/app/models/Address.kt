package com.dhobikart.app.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Address(
    val street: String?,
    val city: String?,
    val state: String?,
    @SerializedName(value="zip", alternate=["zipCode"]) // Handles both 'zip' and 'zipCode'
    val zip: String?
) : Parcelable