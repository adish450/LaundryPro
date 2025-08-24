package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.OffersAdapter
import com.dhobikart.app.adapters.ServiceRecommendationAdapter
import com.dhobikart.app.databinding.FragmentHomeBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_cart -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, CartFragment())
                        .addToBackStack(null)
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    private fun setupClickListeners() {
        binding.upcomingOrderCard.setOnClickListener {
            // If there's an upcoming order, this can navigate to the order details
            // For now, it navigates to the main orders list
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            binding.textWelcome.text = user?.let { "Welcome back, ${it.name}!" } ?: "Welcome!"
        }

        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            val upcomingOrder = orders.firstOrNull { it.status == "Pending" || it.status == "Picked" }
            if (upcomingOrder != null) {
                binding.upcomingOrderCard.visibility = View.VISIBLE
                binding.upcomingOrderTitle.text = "Pickup Scheduled"
                binding.upcomingOrderSubtitle.text = "Tomorrow, 2 PM - 4 PM" // Replace with actual data
            } else {
                binding.upcomingOrderCard.visibility = View.GONE
            }
        }

        viewModel.services.observe(viewLifecycleOwner) { services ->
            val recommendations = services.shuffled().take(2) // Show 2 random services
            binding.recyclerRecommendations.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = ServiceRecommendationAdapter(recommendations) { service ->
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, ItemsFragment.newInstance(service.id, service.name))
                        .addToBackStack(null)
                        .commit()
                }
            }
        }

        viewModel.offers.observe(viewLifecycleOwner) { offers ->
            if (offers.isNullOrEmpty()) {
                binding.recyclerOffers.visibility = View.GONE
            } else {
                binding.recyclerOffers.visibility = View.VISIBLE
                binding.recyclerOffers.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                    adapter = OffersAdapter(offers) {
                        // Handle "Shop Now" click
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, ServicesFragment())
                            .addToBackStack(null)
                            .commit()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
