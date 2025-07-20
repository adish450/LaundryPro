package com.dhobikart.app.data

import com.dhobikart.app.models.LoginResponse
import com.dhobikart.app.models.User
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter

/**
 * This class manually defines how to read and write the LoginResponse JSON.
 * This completely avoids the reflection that Proguard is breaking.
 */
class LoginResponseTypeAdapter(private val gson: Gson) : TypeAdapter<LoginResponse>() {
    override fun write(out: JsonWriter, value: LoginResponse?) {
        // We don't need to write JSON in the client, so this can be left empty.
    }

    override fun read(`in`: JsonReader): LoginResponse {
        var token: String? = null
        var user: User? = null

        `in`.beginObject()
        while (`in`.hasNext()) {
            when (`in`.nextName()) {
                "token" -> token = `in`.nextString()
                "user" -> user = gson.fromJson(`in`, User::class.java)
                else -> `in`.skipValue()
            }
        }
        `in`.endObject()

        if (token == null || user == null) {
            throw IllegalStateException("Token or User is null in LoginResponse")
        }

        return LoginResponse(token, user)
    }
}