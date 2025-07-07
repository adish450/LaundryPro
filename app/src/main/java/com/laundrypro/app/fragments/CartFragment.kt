package com.laundrypro.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.AuthActivity
import com.laundrypro.app.R
import com.laundrypro.app.adapters.CartAdapter
import com.laundrypro.app.adapters.GroupedCartAdapter
import com.laundrypro.app.databinding.FragmentCartBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter
    private lateinit var groupedCartAdapter: GroupedCartAdapter

    private val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            viewModel.checkUserSession()
            goToCheckout()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        groupedCartAdapter = GroupedCartAdapter(
            onQuantityChanged = { itemId, serviceId, newQuantity ->
                viewModel.updateCartItemQuantity(itemId, serviceId, newQuantity)
            },
            onItemRemoved = { itemId, serviceId ->
                viewModel.removeFromCart(itemId, serviceId)
            }
        )
        binding.recyclerCart.layoutManager = LinearLayoutManager(context)
        binding.recyclerCart.adapter = groupedCartAdapter
    }

    private fun setupClickListeners() {
        binding.btnApplyOffer.setOnClickListener {
            val offerCode = binding.etOfferCode.text.toString().trim()
            if (offerCode.isNotEmpty()) {
                if (viewModel.applyOffer(offerCode)) {
                    Toast.makeText(context, "Offer applied successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "Invalid offer code", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            if (viewModel.currentUser.value != null) {
                goToCheckout()
            } else {
                val intent = Intent(activity, AuthActivity::class.java)
                authLauncher.launch(intent)
            }
        }
    }

    private fun goToCheckout() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CheckoutFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun observeViewModel() {
        // The fragment now observes the grouped list
        viewModel.groupedCartItems.observe(viewLifecycleOwner) { groupedItems ->
            groupedCartAdapter.submitList(groupedItems)

            val isEmpty = groupedItems.isNullOrEmpty()
            binding.emptyCartLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
            binding.cartContentLayout.visibility = if (isEmpty) View.GONE else View.VISIBLE
        }

        // The observers for pricing and offers remain the same
        viewModel.appliedOffer.observe(viewLifecycleOwner) { updatePricing() }
        viewModel.cartItems.observe(viewLifecycleOwner) { updatePricing() }

    }

    private fun updatePricing() {
        val summary = viewModel.calculateTotal()
        binding.textSubtotal.text = "₹${String.format("%.2f", summary.subtotal)}"
        binding.textDiscount.text = "-₹${String.format("%.2f", summary.discount)}"
        binding.textTotal.text = "₹${String.format("%.2f", summary.total)}"
        binding.btnCheckout.text = "Proceed to Checkout - ₹${String.format("%.2f", summary.total)}"

        binding.discountLayout.visibility = if (summary.discount > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
