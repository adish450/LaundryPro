package com.dhobikart.app

import android.os.Parcelable
import androidx.annotation.DrawableRes
import kotlinx.parcelize.Parcelize

@Parcelize
data class OnboardingPage(
    val title: String,
    val description: String,
    @DrawableRes val imageResId: Int
) : Parcelable