package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.adapters.OrdersAdapter
import com.laundrypro.app.databinding.FragmentOrdersBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class OrdersFragment : Fragment() {
    private var _binding: FragmentOrdersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var ordersAdapter: OrdersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentOrdersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        // Correctly instantiate the adapter with a lambda for the click listener
        ordersAdapter = OrdersAdapter { order ->
            Toast.makeText(context, "Clicked on order: ${order.id}", Toast.LENGTH_SHORT).show()
            // TODO: Navigate to an Order Details screen if you have one
        }
        binding.recyclerOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerOrders.adapter = ordersAdapter
    }

    private fun observeViewModel() {
        viewModel.orders.observe(viewLifecycleOwner) { orders ->
            if (orders.isNullOrEmpty()) {
                binding.recyclerOrders.visibility = View.GONE
                binding.emptyOrdersLayout.visibility = View.VISIBLE
            } else {
                binding.recyclerOrders.visibility = View.VISIBLE
                binding.emptyOrdersLayout.visibility = View.GONE
                //ordersAdapter.updateOrders(orders)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}