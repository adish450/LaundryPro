package com.dhobikart.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.dhobikart.app.models.User

object SessionManager {
    private const val PREFS_NAME = "DhobiKartApp"
    private const val USER_TOKEN = "user_token"
    private const val USER_DETAILS = "user_details"
    private lateinit var prefs: SharedPreferences
    private val gson = Gson()

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginDetails(token: String, user: User) {
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        val userJson = gson.toJson(user)
        editor.putString(USER_DETAILS, userJson)
        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getUser(): User? {
        val userJson = prefs.getString(USER_DETAILS, null) ?: return null
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
