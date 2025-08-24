package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.OrdersAdapter
import com.dhobikart.app.databinding.FragmentOrderListBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class OrderListFragment : Fragment(R.layout.fragment_order_list) {
    private var _binding: FragmentOrderListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private var isCurrentOrders: Boolean = true

    companion object {
        private const val IS_CURRENT_ORDERS = "is_current_orders"
        fun newInstance(isCurrent: Boolean): OrderListFragment {
            return OrderListFragment().apply {
                arguments = Bundle().apply {
                    putBoolean(IS_CURRENT_ORDERS, isCurrent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            isCurrentOrders = it.getBoolean(IS_CURRENT_ORDERS)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderListBinding.bind(view)

        val ordersAdapter = OrdersAdapter { order ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrderDetailsFragment.newInstance(order.id))
                .addToBackStack(null)
                .commit()
        }

        binding.recyclerOrders.layoutManager = LinearLayoutManager(context)
        binding.recyclerOrders.adapter = ordersAdapter

        viewModel.orders.observe(viewLifecycleOwner) { allOrders ->
            val filteredOrders = if (isCurrentOrders) {
                allOrders.filter { it.status != "Completed" && it.status != "Cancelled" }
            } else {
                allOrders.filter { it.status == "Completed" || it.status == "Cancelled" }
            }
            ordersAdapter.submitList(filteredOrders)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
