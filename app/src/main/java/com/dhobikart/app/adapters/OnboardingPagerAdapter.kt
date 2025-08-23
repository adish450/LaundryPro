package com.dhobikart.app.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dhobikart.app.OnboardingPage
import com.dhobikart.app.R
import com.dhobikart.app.fragments.OnboardingPageFragment

class OnboardingPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {

    private val pages = listOf(
        OnboardingPage(
            title = "Laundry, simplified",
            description = "Schedule pickups, track orders, and manage your laundry preferences with ease.",
            imageResId = R.drawable.ic_onboarding_1
        ),
        OnboardingPage(
            title = "Laundry, Your Way",
            description = "Schedule pickups and customize your laundry preferences with ease. Choose your preferred detergents, folding instructions, and more.",
            imageResId = R.drawable.ic_onboarding_2
        ),
        OnboardingPage(
            title = "Track Your Laundry",
            description = "Stay updated on your laundry's journey with real-time tracking and notifications.",
            imageResId = R.drawable.ic_onboarding_3
        )
    )

    override fun getItemCount(): Int = pages.size

    override fun createFragment(position: Int): Fragment {
        return OnboardingPageFragment.newInstance(pages[position])
    }
}