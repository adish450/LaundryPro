package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.databinding.FragmentManageAddressesBinding
import com.dhobikart.app.adapters.ManageAddressAdapter
import com.dhobikart.app.viewmodels.LaundryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ManageAddressesFragment : Fragment() {

    private var _binding: FragmentManageAddressesBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var addressAdapter: ManageAddressAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentManageAddressesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerView() {
        addressAdapter = ManageAddressAdapter(
            onEditClicked = { address ->
                AddEditAddressDialogFragment.newInstance(address).show(parentFragmentManager, "edit_address")
            },
            onDeleteClicked = { address ->
                showDeleteConfirmationDialog(address)
            }
        )
        binding.recyclerAddresses.layoutManager = LinearLayoutManager(context)
        binding.recyclerAddresses.adapter = addressAdapter
    }

    private fun setupClickListeners() {
        binding.fabAddAddress.setOnClickListener {
            AddEditAddressDialogFragment.newInstance().show(parentFragmentManager, "add_address")
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val addresses = user?.address ?: emptyList()
            if (addresses.isEmpty()) {
                binding.recyclerAddresses.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerAddresses.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                addressAdapter.submitList(addresses)
            }
        }
    }

    private fun showDeleteConfirmationDialog(address: com.dhobikart.app.models.Address) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Delete Address")
            .setMessage("Are you sure you want to delete this address?")
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteAddress(address)
            }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}