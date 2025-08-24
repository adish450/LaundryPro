package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.ServicesAdapter
import com.dhobikart.app.databinding.FragmentServicesBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class ServicesFragment : Fragment(R.layout.fragment_services) {

    private var _binding: FragmentServicesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var servicesAdapter: ServicesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentServicesBinding.bind(view)

        setupToolbar()
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        servicesAdapter = ServicesAdapter { service ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ItemsFragment.newInstance(service.id.toString(), service.name))
                .addToBackStack(null)
                .commit()
        }
        binding.recyclerServices.layoutManager = LinearLayoutManager(context)
        binding.recyclerServices.adapter = servicesAdapter
    }

    private fun observeViewModel() {
        viewModel.services.observe(viewLifecycleOwner) { services ->
            servicesAdapter.submitList(services)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}