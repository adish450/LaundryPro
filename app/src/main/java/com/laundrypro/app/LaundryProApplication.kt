package com.laundrypro.app

import android.app.Application
import com.laundrypro.app.data.AdminSessionManager
import com.laundrypro.app.data.CartManager
import com.laundrypro.app.data.SessionManager

class LaundryProApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the SessionManager once when the app starts
        SessionManager.init(this)
        CartManager.init(this)
        AdminSessionManager.init(this)
    }
}