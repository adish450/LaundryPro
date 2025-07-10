package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.ItemsAdapter
import com.dhobikart.app.databinding.FragmentItemsBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var itemsAdapter: ItemsAdapter
    private var serviceId: String = ""

    companion object {
        private const val ARG_SERVICE_ID = "service_id"
        fun newInstance(serviceId: String): ItemsFragment {
            return ItemsFragment().apply {
                arguments = Bundle().apply { putString(ARG_SERVICE_ID, serviceId) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getString(ARG_SERVICE_ID, "")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentItemsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        if (serviceId.isNotEmpty()) {
            viewModel.loadItemsForService(serviceId)
        }
        binding.btnViewCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        itemsAdapter = ItemsAdapter { serviceCloth ->
            viewModel.addToCart(serviceCloth, serviceId)
            Toast.makeText(context, "${serviceCloth.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerItems.layoutManager = LinearLayoutManager(context)
        binding.recyclerItems.adapter = itemsAdapter

    }

    private fun observeViewModel() {
        viewModel.serviceCloths.observe(viewLifecycleOwner) { items ->
            itemsAdapter.submitList(items)
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            val itemCount = cartItems?.sumOf { it.quantity } ?: 0
            if (itemCount > 0) {
                binding.btnViewCart.text = "View Cart ($itemCount items)"
                binding.btnViewCart.visibility = View.VISIBLE
            } else {
                binding.btnViewCart.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
