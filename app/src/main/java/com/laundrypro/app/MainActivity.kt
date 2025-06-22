package com.laundrypro.app

import android.content.Intent
import android.os.Bundle
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

        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }

        observeNavigation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkUserSession()
    }

    private fun observeNavigation() {
        viewModel.navigateToHome.observe(this) { navigate ->
            if (navigate == true) {
                binding.bottomNavigation.selectedItemId = R.id.nav_home
                viewModel.onHomeNavigationComplete()
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_cart -> {
                    loadFragment(CartFragment())
                    true
                }
                R.id.nav_orders, R.id.nav_profile -> {
                    if (viewModel.currentUser.value != null) {
                        // User is logged in, navigate to the selected fragment
                        val fragment = if (item.itemId == R.id.nav_orders) OrdersFragment() else ProfileFragment()
                        loadFragment(fragment)
                        true
                    } else {
                        // User is not logged in, launch AuthActivity to prompt for login
                        startActivity(Intent(this, AuthActivity::class.java))
                        false // Return false to prevent the tab selection from changing
                    }
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
}