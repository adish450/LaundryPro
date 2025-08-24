package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.dhobikart.app.R
import com.dhobikart.app.adapters.OrdersPagerAdapter
import com.dhobikart.app.databinding.FragmentOrdersBinding

class OrdersFragment : Fragment(R.layout.fragment_orders) {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrdersBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val pagerAdapter = OrdersPagerAdapter(this)
        binding.viewPager.adapter = pagerAdapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = if (position == 0) "Current" else "Past"
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
