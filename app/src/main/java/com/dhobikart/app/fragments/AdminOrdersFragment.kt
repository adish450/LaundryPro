package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.R
import com.laundrypro.app.adapters.AdminOrdersAdapter
import com.laundrypro.app.databinding.FragmentAdminOrdersBinding
import com.laundrypro.app.viewmodels.AdminViewModel

class AdminOrdersFragment : Fragment(R.layout.fragment_admin_orders) {
    private var _binding: FragmentAdminOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AdminViewModel by viewModels()
    private lateinit var adminOrdersAdapter: AdminOrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAdminOrdersBinding.bind(view)

        setupRecyclerView()
        viewModel.allOrders.observe(viewLifecycleOwner) { orders ->
            adminOrdersAdapter.submitList(orders)
        }

        viewModel.fetchAllOrders()
    }

    private fun setupRecyclerView() {
        adminOrdersAdapter = AdminOrdersAdapter()
        binding.recyclerAllOrders.apply {
            adapter = adminOrdersAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}