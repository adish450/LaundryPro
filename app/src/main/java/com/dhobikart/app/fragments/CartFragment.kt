package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.adapters.GroupedCartAdapter
import com.dhobikart.app.databinding.FragmentCartBinding
import com.dhobikart.app.models.CartDisplayItem
import com.dhobikart.app.models.CartItem
import com.dhobikart.app.models.Offer
import com.dhobikart.app.viewmodels.LaundryViewModel
import java.text.NumberFormat
import java.util.Locale

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var cartAdapter: GroupedCartAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        binding.btnApplyPromo.setOnClickListener {
            val code = binding.etPromoCode.text.toString().trim()
            Toast.makeText(context, "Applying code...", Toast.LENGTH_SHORT).show()
            viewModel.applyPromoCode(code)
        }

        binding.btnRemovePromo.setOnClickListener {
            viewModel.removePromoCode()
            binding.etPromoCode.text.clear()
        }

        binding.btnSelectAddress.setOnClickListener {
            val cartList = viewModel.cartItems.value
            val offer = viewModel.appliedOffer.value

            val subtotal = cartList?.sumOf { it.price * it.quantity } ?: 0.0
            val deliveryCharges = 0.00 // Example
            var discount = 0.0
            if (offer != null) {
                val percentageDiscount = (subtotal * offer.discountPercentage) / 100.0
                //val maxDiscount = offer.maxDiscount.toDoubleOrNull() ?: Double.MAX_VALUE
                discount = percentageDiscount
            }
            val total = (subtotal + deliveryCharges - discount).coerceAtLeast(0.0)

            //val checkoutFragment = CheckoutFragment.newInstance(subtotal, discount, total)

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CheckoutFragment()) // Navigate to CheckoutFragment
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        cartAdapter = GroupedCartAdapter( // <-- Instantiate the renamed adapter
            onUpdateQuantity = { cartItem, newQuantity ->
                viewModel.updateCartItemQuantity(cartItem.itemId, cartItem.serviceId, newQuantity)
            },
            onDeleteItem = { cartItem ->
                viewModel.deleteItemFromCart(cartItem.itemId, cartItem.serviceId)
            }
        )
        binding.recyclerCartItems.adapter = cartAdapter
    }


    private fun observeViewModel() {
        // Observer 1: Watches for changes to the list of items in the cart
        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            // When the cart changes, we need to rebuild the display list
            updateAdapterWithCartData(cartList)
            // And recalculate the payment summary, using the current value of the applied offer
            updatePaymentSummary(cartList, viewModel.appliedOffer.value)
        }

        // Observer 2: Watches for changes to the applied promo code
        viewModel.appliedOffer.observe(viewLifecycleOwner) { offer ->
            // When the offer changes, we only need to recalculate the summary
            // using the current value of the cart items
            updatePaymentSummary(viewModel.cartItems.value, offer)
        }

        // Observer 3: Watches for status messages (like "Invalid code")
        viewModel.promoStatus.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // This new helper function contains the logic to build the adapter's list
    private fun updateAdapterWithCartData(cartList: List<CartItem>?) {
        val services = viewModel.services.value
        if (cartList.isNullOrEmpty() || services.isNullOrEmpty()) {
            binding.recyclerCartItems.visibility = View.GONE
            return
        }

        binding.recyclerCartItems.visibility = View.VISIBLE

        val serviceNameMap = services.associateBy({ it.id }, { it.name })
        val groupedById = cartList.groupBy { it.serviceId }

        val displayList = mutableListOf<CartDisplayItem>()
        groupedById.forEach { (serviceId, items) ->
            val serviceName = serviceNameMap[serviceId] ?: "Other Items"
            displayList.add(CartDisplayItem.Header(serviceName))
            items.forEach { cartItem ->
                displayList.add(CartDisplayItem.Item(cartItem))
            }
            val groupSubtotal = items.sumOf { it.price * it.quantity }
            displayList.add(CartDisplayItem.Footer(groupSubtotal))
        }
        cartAdapter.submitList(displayList)
    }

    private fun updatePaymentSummary(cartList: List<CartItem>?, offer: Offer?) {
        val subtotal = cartList?.sumOf { it.price * it.quantity } ?: 0.0
        val deliveryCharges =0.00 // Example

        var discount = 0.0
        if (offer != null) {
            // Calculate discount... (logic is unchanged)
            val percentageDiscount = (subtotal * offer.discountPercentage) / 100.0
            //val maxDiscount = offer.maxDiscount.toDoubleOrNull() ?: Double.MAX_VALUE
            discount = percentageDiscount

            // --- CORRECTED UI BINDING ---
            binding.layoutDiscount.visibility = View.VISIBLE
            binding.tvDiscountLabel.text = "Discount (${offer.code})" // Set the label with the code
            binding.tvDiscountAmount.text = "-${NumberFormat.getCurrencyInstance(Locale("en", "IN")).format(discount)}" // Set the amount

        } else {
            binding.layoutDiscount.visibility = View.GONE
        }

        val total = (subtotal + deliveryCharges - discount).coerceAtLeast(0.0)

        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

        binding.tvSubtotal.text = format.format(subtotal)
        binding.tvDeliveryCharges.text = format.format(deliveryCharges)
        binding.tvTotal.text = format.format(total)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}