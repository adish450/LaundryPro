package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentOrderSuccessBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class OrderSuccessFragment : Fragment(R.layout.fragment_order_success) {

    private var _binding: FragmentOrderSuccessBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        fun newInstance(orderId: String): OrderSuccessFragment {
            return OrderSuccessFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_ORDER_ID, orderId)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderSuccessBinding.bind(view)

        val orderId = arguments?.getString(ARG_ORDER_ID) ?: "N/A"
        binding.textOrderIdSummary.text = "Your Order ID is #${orderId.takeLast(6)}"

        binding.btnTrackOrder.setOnClickListener {
            // Navigate to the "My Orders" screen
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, OrdersFragment())
                .commit()
        }

        binding.btnBackToHome.setOnClickListener {
            // Navigate back to the home screen
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
