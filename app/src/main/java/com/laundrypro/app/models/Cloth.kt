package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

// This model represents a single cloth item, based on your Cloth.js schema.
data class Cloth(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val imageUrl: String?
)