package com.laundrypro.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    private var serviceId: Int = 0

    companion object {
        private const val ARG_SERVICE_ID = "service_id"

        fun newInstance(serviceId: Int): ItemsFragment {
            val fragment = ItemsFragment()
            val args = Bundle()
            args.putInt(ARG_SERVICE_ID, serviceId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        serviceId = arguments?.getInt(ARG_SERVICE_ID) ?: 0
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
        itemsAdapter = ItemsAdapter { item ->
            viewModel.addToCart(item, serviceId)
        }
        binding.recyclerItems.layoutManager = LinearLayoutManager(context)
        binding.recyclerItems.adapter = itemsAdapter
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            val service = services.find { it.id == serviceId }
            service?.let {
                binding.toolbarTitle.text = it.name
                itemsAdapter.updateItems(it.items)
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            val itemCount = cartItems.sumOf { it.quantity }
            binding.btnViewCart.text = "View Cart ($itemCount items)"
            binding.btnViewCart.visibility = if (itemCount > 0) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}