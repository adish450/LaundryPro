package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.AdminOrdersAdapter
import com.dhobikart.app.databinding.FragmentAdminOrdersBinding
import com.dhobikart.app.viewmodels.AdminViewModel

class AdminOrdersFragment : Fragment(R.layout.fragment_admin_orders) {

    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by activityViewModels()
    private lateinit var ordersAdapter: AdminOrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminOrdersBinding.bind(view)

        setupRecyclerView()
        observeViewModel()
        viewModel.loadAllOrders()
    }

    private fun setupRecyclerView() {
        ordersAdapter = AdminOrdersAdapter { order ->
            // Handle order click if needed
        }
        binding.recyclerAllOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerAllOrders.adapter = ordersAdapter
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            ordersAdapter.submitList(orders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}