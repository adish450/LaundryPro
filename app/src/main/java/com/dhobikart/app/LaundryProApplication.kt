package com.dhobikart.app

import android.app.Application
import com.dhobikart.app.data.AdminSessionManager
import com.dhobikart.app.data.CartManager
import com.dhobikart.app.data.SessionManager

class DhobikartApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the SessionManager once when the app starts
        SessionManager.init(this)
        CartManager.init(this)
        AdminSessionManager.init(this)
    }
}