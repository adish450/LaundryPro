package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.PaymentMethodsAdapter
import com.dhobikart.app.databinding.FragmentPaymentMethodsBinding
import com.dhobikart.app.models.PaymentMethod
import com.dhobikart.app.viewmodels.LaundryViewModel

class PaymentMethodsFragment : Fragment(R.layout.fragment_payment_methods) {

    private var _binding: FragmentPaymentMethodsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentPaymentMethodsBinding.bind(view)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        val paymentMethodsAdapter = PaymentMethodsAdapter {
            // Handle click on a payment method
        }

        binding.recyclerPaymentMethods.layoutManager = LinearLayoutManager(context)
        binding.recyclerPaymentMethods.adapter = paymentMethodsAdapter

        // TODO: Replace with actual data from ViewModel
        val dummyData = listOf(
            PaymentMethod("Credit Card", "Visa ... 4567", R.drawable.ic_visa),
            PaymentMethod("Digital Wallet", "PayPal", R.drawable.ic_paypal),
            PaymentMethod("Credit Card", "MasterCard ... 8901", R.drawable.ic_mastercard)
        )
        paymentMethodsAdapter.submitList(dummyData)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
