package com.dhobikart.app.data

import android.content.Context
import android.content.SharedPreferences
import com.dhobikart.app.models.User
import com.google.gson.Gson

object AdminSessionManager {
    private const val PREFS_NAME = "DhobiKartAdmin"
    private const val ADMIN_TOKEN = "admin_token"
    private const val ADMIN_DETAILS = "admin_details"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginDetails(token: String, user: User) {
        val editor = prefs.edit()
        editor.putString(ADMIN_TOKEN, token)
        val userJson = gson.toJson(user)
        editor.putString(ADMIN_DETAILS, userJson)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(ADMIN_TOKEN, null)
    }

    fun getUser(): User? {
        val userJson = prefs.getString(ADMIN_DETAILS, null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}