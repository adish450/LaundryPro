package com.dhobikart.app.models

// A helper class to combine a Cloth and its specific price for a given service
data class PricedClothItem(
    val cloth: Cloth,
    val price: Double
)