package com.laundrypro.app

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayoutMediator
import com.laundrypro.app.adapters.AuthViewPagerAdapter
import com.laundrypro.app.databinding.ActivityAuthBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class AuthActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthBinding
    private val viewModel: LaundryViewModel by viewModels() // Initialize the ViewModel here

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup ViewPager Adapter
        val adapter = AuthViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        // Link TabLayout with ViewPager
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Login"
                1 -> "Register"
                else -> null
            }
        }.attach()
    }
}
