package com.laundrypro.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.laundrypro.app.fragments.AdminOrdersFragment

class AdminDashboardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard) // A simple layout with a FrameLayout container

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.admin_fragment_container, AdminOrdersFragment())
                .commit()
        }
    }
}
