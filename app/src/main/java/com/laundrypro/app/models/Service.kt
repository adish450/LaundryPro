package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// This model represents a single service, based on your Service.js schema.
data class Service(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String?
)