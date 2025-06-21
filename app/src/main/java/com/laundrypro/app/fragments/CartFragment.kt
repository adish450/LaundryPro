package com.laundrypro.app.fragments

import android.app.Activity.RESULT_OK
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
import com.laundrypro.app.databinding.FragmentCartBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var cartAdapter: CartAdapter

    private val authLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        // This callback runs when we return from AuthActivity
        if (result.resultCode == RESULT_OK) {
            // The user successfully logged in. Refresh the session state.
            viewModel.checkUserSession()
            // Now, we can safely proceed to checkout.
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
        cartAdapter = CartAdapter(
            onQuantityChanged = { itemId, serviceId, quantity ->
                viewModel.updateCartItemQuantity(itemId, serviceId, quantity)
            },
            onItemRemoved = { itemId, serviceId ->
                viewModel.removeFromCart(itemId, serviceId)
            }
        )
        binding.recyclerCart.layoutManager = LinearLayoutManager(context)
        binding.recyclerCart.adapter = cartAdapter
    }

    private fun setupClickListeners() {
        binding.btnApplyOffer.setOnClickListener {
            val offerCode = binding.etOfferCode.text.toString().trim()
            if (offerCode.isNotEmpty()) {
                val applied = viewModel.applyOffer(offerCode)
                if (applied) {
                    Toast.makeText(context, "Offer applied successfully!", Toast.LENGTH_SHORT).show()
                    binding.etOfferCode.text?.clear()
                } else {
                    Toast.makeText(context, "Invalid offer code", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnCheckout.setOnClickListener {
            if (viewModel.currentUser.value != null) {
                goToCheckout()
            } else {
                // The ActivityResultLauncher logic for login remains the same
                val intent = Intent(activity, AuthActivity::class.java)
                authLauncher.launch(intent)
            }
        }
    }

    private fun goToCheckout() {
        // This is the updated navigation logic
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, CheckoutFragment())
            .addToBackStack(null) // This allows the user to press back to return to the cart
            .commit()
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            cartAdapter.updateCart(cartItems)
            binding.emptyCartLayout.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
            binding.cartContentLayout.visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.appliedOffer.observe(viewLifecycleOwner) { offer ->
            binding.appliedOfferText.text = offer?.title ?: ""
            binding.appliedOfferText.visibility = if (offer != null) View.VISIBLE else View.GONE
        }

        // Update pricing
        viewModel.cartItems.observe(viewLifecycleOwner) {
            updatePricing()
        }
        viewModel.appliedOffer.observe(viewLifecycleOwner) {
            updatePricing()
        }
    }

    private fun updatePricing() {
        val summary = viewModel.calculateTotal()
        binding.textSubtotal.text = "$${String.format("%.2f", summary.subtotal)}"
        binding.textDiscount.text = "-$${String.format("%.2f", summary.discount)}"
        binding.textTotal.text = "$${String.format("%.2f", summary.total)}"
        binding.btnCheckout.text = "Proceed to Checkout - $${String.format("%.2f", summary.total)}"

        binding.discountLayout.visibility = if (summary.discount > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}