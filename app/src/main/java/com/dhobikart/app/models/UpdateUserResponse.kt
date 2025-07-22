package com.dhobikart.app.models

/**
 * This data class exactly matches the JSON response from the server
 * when a user's profile is updated.
 */
data class UpdateUserResponse(
    val message: String,
    val user: User
)