package com.laundrypro.app

import android.app.Application
import com.laundrypro.app.data.SessionManager

class LaundryProApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize the SessionManager once when the app starts
        SessionManager.init(this)
    }
}