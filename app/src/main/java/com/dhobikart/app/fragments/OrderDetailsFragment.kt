package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.databinding.FragmentOrderDetailsBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class OrderDetailsFragment : Fragment(R.layout.fragment_order_details) {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private var orderId: String? = null

    companion object {
        private const val ORDER_ID = "order_id"
        fun newInstance(orderId: String): OrderDetailsFragment {
            return OrderDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(ORDER_ID, orderId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            orderId = it.getString(ORDER_ID)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentOrderDetailsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        /*orderId?.let {
            viewModel.getOrderDetails(it)
            viewModel.orderDetails.observe(viewLifecycleOwner) { order ->
                // TODO: Populate the UI with order details
            }
        }*/
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
