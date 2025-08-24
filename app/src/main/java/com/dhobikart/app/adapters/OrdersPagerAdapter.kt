package com.dhobikart.app.adapters

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.dhobikart.app.fragments.OrderListFragment

class OrdersPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment {
        return OrderListFragment.newInstance(position == 0)
    }
}
