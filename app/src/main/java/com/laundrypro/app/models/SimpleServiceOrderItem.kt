package com.laundrypro.app.models

import com.google.gson.annotations.SerializedName

/**
 * Represents a service within the simple order response from the server.
 */
data class SimpleServiceOrderItem(
    @SerializedName("_id")
    val id: String,
    val serviceId: String,
    val clothes: List<SimpleClothOrderItem>
)