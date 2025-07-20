package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.OffersAdapter
import com.dhobikart.app.adapters.ServicesAdapter
import com.dhobikart.app.databinding.FragmentHomeBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var servicesAdapter: ServicesAdapter
    private lateinit var offersAdapter: OffersAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerViews()
        observeViewModel()

        // Start the shimmer animations
        binding.shimmerOffers.startShimmer()
        binding.shimmerServices.startShimmer()

        viewModel.loadServices()
        binding.cartIconContainer.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, CartFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupRecyclerViews() {
        offersAdapter = OffersAdapter { offer ->
            viewModel.applyOffer(offer.code)
        }
        binding.recyclerOffers.adapter = offersAdapter

        servicesAdapter = ServicesAdapter { service ->
            // **THE FIX:** Pass both the service ID and the service name.
            val fragment = ItemsFragment.newInstance(service.id, service.name)
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerServices.layoutManager = GridLayoutManager(context, 2)
        binding.recyclerServices.adapter = servicesAdapter
    }

    private fun observeViewModel() {
        viewModel.offers.observe(viewLifecycleOwner) { offers ->
            // Stop shimmer and show the recycler view for offers
            binding.shimmerOffers.stopShimmer()
            binding.shimmerOffers.visibility = View.GONE
            binding.recyclerOffers.visibility = View.VISIBLE
            offersAdapter.updateOffers(offers)
        }

        viewModel.services.observe(viewLifecycleOwner) { services ->
            // Stop shimmer and show the recycler view for services
            binding.shimmerServices.stopShimmer()
            binding.shimmerServices.visibility = View.GONE
            binding.recyclerServices.visibility = View.VISIBLE
            servicesAdapter.updateServices(services)
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { cartItems ->
            val itemCount = cartItems?.sumOf { it.quantity } ?: 0
            if (itemCount > 0) {
                binding.cartBadge.text = itemCount.toString()
                binding.cartBadge.visibility = View.VISIBLE
            } else {
                binding.cartBadge.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}