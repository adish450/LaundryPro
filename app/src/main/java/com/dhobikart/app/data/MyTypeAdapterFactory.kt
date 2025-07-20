package com.dhobikart.app.data

import com.dhobikart.app.models.LoginResponse
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

/**
 * This factory tells Gson to use our custom TypeAdapter for the LoginResponse class.
 */
class MyTypeAdapterFactory : TypeAdapterFactory {
    override fun <T : Any?> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        if (type.rawType == LoginResponse::class.java) {
            @Suppress("UNCHECKED_CAST")
            return LoginResponseTypeAdapter(gson) as TypeAdapter<T>
        }
        return null
    }
}