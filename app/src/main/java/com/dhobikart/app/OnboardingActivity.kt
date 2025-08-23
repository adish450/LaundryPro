package com.dhobikart.app

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.dhobikart.app.adapters.OnboardingPagerAdapter
import com.dhobikart.app.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var onboardingPagerAdapter: OnboardingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onboardingPagerAdapter = OnboardingPagerAdapter(this)
        binding.viewPager.adapter = onboardingPagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (position == onboardingPagerAdapter.itemCount - 1) {
                    binding.btnGetStarted.text = "Get Started"
                } else {
                    binding.btnGetStarted.text = "Next"
                }
            }
        })

        binding.btnGetStarted.setOnClickListener {
            if (binding.viewPager.currentItem < onboardingPagerAdapter.itemCount - 1) {
                binding.viewPager.currentItem += 1
            } else {
                val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
                with(sharedPref.edit()) {
                    putBoolean("isFinished", true)
                    apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}