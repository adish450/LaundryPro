package com.dhobikart.app.models

import androidx.annotation.DrawableRes

data class PaymentMethod(
    val name: String,
    val details: String,
    @DrawableRes val iconResId: Int
)
