package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

data class Service(
    @SerializedName("_id")
    val id: String,
    val name: String,
    val description: String?
)