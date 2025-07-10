package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.OrdersAdapter
import com.dhobikart.app.databinding.FragmentOrdersBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class OrdersFragment : Fragment(R.layout.fragment_orders) {

    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrdersBinding.bind(view)

        setupRecyclerView()
        observeViewModel()

        binding.btnPlaceNewOrder.setOnClickListener {
            // Navigate back to the home screen to place a new order
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

        viewModel.loadUserOrders()
    }

    private fun setupRecyclerView() {
        ordersAdapter = OrdersAdapter { order ->
            Toast.makeText(context, "Clicked on order: ${order.id}", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerOrders.adapter = ordersAdapter
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNullOrEmpty()) {
                binding.emptyOrdersLayout.visibility = View.VISIBLE
                binding.recyclerOrders.visibility = View.GONE
            } else {
                binding.emptyOrdersLayout.visibility = View.GONE
                binding.recyclerOrders.visibility = View.VISIBLE
                ordersAdapter.submitList(orders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}