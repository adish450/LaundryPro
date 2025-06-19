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
    private val viewModel: LaundryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = AuthViewPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Login"
                1 -> "Register"
                else -> null
            }
        }.attach()

        observeNavigation()
    }

    private fun observeNavigation() {
        viewModel.navigateToTab.observe(this) { tabIndex ->
            tabIndex?.let {
                binding.viewPager.currentItem = it
                viewModel.onNavigationComplete() // Reset the event
            }
        }
    }
}