package com.laundrypro.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.laundrypro.app.models.User

object AdminSessionManager {
    private const val PREFS_NAME = "LaundryProAdminApp"
    private const val ADMIN_TOKEN = "admin_token"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(ADMIN_TOKEN, token)
        editor.commit()
    }

    fun getToken(): String? {
        return prefs.getString(ADMIN_TOKEN, null)
    }

    fun clear() {
        prefs.edit().clear().apply()
    }
}
