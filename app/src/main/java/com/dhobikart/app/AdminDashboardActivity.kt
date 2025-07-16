package com.dhobikart.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dhobikart.app.fragments.AdminOrdersFragment

class AdminDashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AdminOrdersFragment())
                .commit()
        }
    }
}