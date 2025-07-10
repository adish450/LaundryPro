package com.dhobikart.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.adapters.AddressAdapter
import com.dhobikart.app.databinding.DialogFragmentSelectAddressBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class SelectAddressDialogFragment : DialogFragment() {

    private var _binding: DialogFragmentSelectAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    companion object {
        const val TAG = "SelectAddressDialog"
        const val REQUEST_KEY = "SELECT_ADDRESS_REQUEST"
        const val KEY_ADDRESS = "SELECTED_ADDRESS"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogFragmentSelectAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        val addressAdapter = AddressAdapter { address ->
            setFragmentResult(REQUEST_KEY, bundleOf(KEY_ADDRESS to address))
            dismiss()
        }
        binding.recyclerAddresses.layoutManager = LinearLayoutManager(context)
        binding.recyclerAddresses.adapter = addressAdapter
    }

    private fun observeViewModel() {
        viewModel.previousAddresses.observe(viewLifecycleOwner) { addresses ->
            (binding.recyclerAddresses.adapter as AddressAdapter).submitList(addresses)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}