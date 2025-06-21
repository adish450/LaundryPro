package com.laundrypro.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.laundrypro.app.models.User

object SessionManager {
    private const val PREFS_NAME = "LaundryProApp"
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
        // Use commit() to save data synchronously and avoid race conditions
        editor.commit()
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
        // Use commit() for synchronous clearing
        prefs.edit().clear().commit()
    }
}