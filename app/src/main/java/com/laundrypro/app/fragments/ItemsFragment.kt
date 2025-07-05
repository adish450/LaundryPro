package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.R
import com.laundrypro.app.adapters.ItemsAdapter
import com.laundrypro.app.databinding.FragmentItemsBinding
import com.laundrypro.app.viewmodels.LaundryViewModel

class ItemsFragment : Fragment() {
    private var _binding: FragmentItemsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var itemsAdapter: ItemsAdapter
    private var serviceId: String = ""

    companion object {
        private const val ARG_SERVICE_ID = "service_id"

        fun newInstance(serviceId: Int): ItemsFragment {
            return ItemsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SERVICE_ID, serviceId)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            serviceId = it.getInt(ARG_SERVICE_ID).toString()
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

        binding.btnViewCart.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerView() {
        // This is the fix: The adapter's click listener now calls the ViewModel
        itemsAdapter = ItemsAdapter { item ->
            viewModel.addToCart(item, serviceId)
            Toast.makeText(context, "${item.name} added to cart", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerItems.layoutManager = LinearLayoutManager(context)
        binding.recyclerItems.adapter = itemsAdapter
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            val service = services.find { it.id.toString() == serviceId }
            service?.let {
                binding.toolbarTitle.text = it.name
                itemsAdapter.updateItems(it.items)
            }
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
