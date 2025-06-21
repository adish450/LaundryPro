package com.laundrypro.app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.laundrypro.app.databinding.ActivityMainBinding
import com.laundrypro.app.fragments.*
import com.laundrypro.app.viewmodels.LaundryViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: LaundryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // The 'by viewModels()' delegate handles creating the ViewModel correctly
        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        viewModel.currentUser.observe(this) { user ->
            invalidateOptionsMenu()
        }
    }

    override fun onResume() {
        super.onResume()
        // This is the key fix: Refresh the user session every time
        // the activity comes to the foreground (e.g., after returning from login).
        viewModel.checkUserSession()
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_orders -> {
                    if (viewModel.currentUser.value != null) {
                        loadFragment(OrdersFragment())
                    } else {
                        startActivity(Intent(this, AuthActivity::class.java))
                    }
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_profile -> {
                    if (viewModel.currentUser.value != null) {
                        loadFragment(ProfileFragment())
                    } else {
                        startActivity(Intent(this, AuthActivity::class.java))
                    }
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {
                // Handle notifications
                true
            }
            R.id.action_login -> {
                startActivity(Intent(this, AuthActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}