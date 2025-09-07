package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.R
import com.dhobikart.app.adapters.ItemsAdapter
import com.dhobikart.app.databinding.FragmentItemsBinding
import com.dhobikart.app.models.CartItem
import com.dhobikart.app.viewmodels.LaundryViewModel
import java.text.NumberFormat
import java.util.Locale

class ItemsFragment : Fragment(R.layout.fragment_items) {

    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var itemsAdapter: ItemsAdapter

    private var serviceId: String? = null
    private var serviceName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getString(ARG_SERVICE_ID)
            serviceName = it.getString(ARG_SERVICE_NAME)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentItemsBinding.bind(view)

        setupToolbar()
        setupRecyclerView(emptyMap())
        observeViewModel()

        binding.btnViewCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }

        serviceId?.let { viewModel.getClothesForService(it) }
    }

    private fun setupToolbar() {
        binding.toolbar.title = serviceName
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView(cartMap: Map<String, CartItem>) {
        itemsAdapter = ItemsAdapter(
            cartMap,
            onAddItem = { item ->
                serviceId?.let { sid -> viewModel.addItemToCart(item, sid) }
            },
            onRemoveItem = { item ->
                serviceId?.let { sid -> viewModel.removeItemFromCart(item, sid) }
            }
        )
        binding.recyclerItems.adapter = itemsAdapter
    }

    private fun observeViewModel() {
        viewModel.serviceClothes.observe(viewLifecycleOwner) { clothes ->
            itemsAdapter.submitList(clothes)
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartList ->
            // Filter the global cart to only include items for the current service.
            val serviceSpecificCart = cartList.filter { it.serviceId == serviceId }
            val cartMap = serviceSpecificCart.associateBy { it.itemId }

            setupRecyclerView(cartMap)
            itemsAdapter.submitList(viewModel.serviceClothes.value)

            // The "View Cart" button should still reflect the total for all services.
            if (cartList.isEmpty()) {
                binding.btnViewCart.visibility = View.GONE
            } else {
                val totalItemCount = cartList.sumOf { it.quantity }
                val totalPrice = cartList.sumOf { (it.price) * it.quantity }
                val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

                binding.btnViewCart.visibility = View.VISIBLE
                binding.btnViewCart.text = "View Cart ($totalItemCount items) - ${format.format(totalPrice)}"
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SERVICE_ID = "service_id"
        private const val ARG_SERVICE_NAME = "service_name"

        @JvmStatic
        fun newInstance(serviceId: String, serviceName: String) =
            ItemsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_SERVICE_ID, serviceId)
                    putString(ARG_SERVICE_NAME, serviceName)
                }
            }
    }
}