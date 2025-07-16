package com.dhobikart.app.fragments

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.R
import com.dhobikart.app.adapters.AddressAdapter
import com.dhobikart.app.databinding.DialogCustomSelectAddressBinding
import com.dhobikart.app.viewmodels.LaundryViewModel

class SelectAddressDialogFragment : DialogFragment() {

    private var _binding: DialogCustomSelectAddressBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var addressAdapter: AddressAdapter

    companion object {
        const val TAG = "SelectAddressDialog"
        const val REQUEST_KEY = "SELECT_ADDRESS_REQUEST"
        const val KEY_ADDRESS = "SELECTED_ADDRESS"
    }

    // Apply the custom dialog theme.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.Theme_DhobiKart_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogCustomSelectAddressBinding.inflate(inflater, container, false)
        // **THE FIX:** Ensure the dialog's window background is transparent.
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        setupClickListeners()
    }

    private fun setupRecyclerView() {
        addressAdapter = AddressAdapter()
        binding.recyclerAddresses.layoutManager = LinearLayoutManager(context)
        binding.recyclerAddresses.adapter = addressAdapter
    }

    private fun setupClickListeners() {
        binding.btnClose.setOnClickListener { dismiss() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnUse.setOnClickListener {
            val selectedAddress = addressAdapter.getSelectedAddress()
            selectedAddress?.let {
                setFragmentResult(REQUEST_KEY, Bundle().apply {
                    putParcelable(KEY_ADDRESS, it as Parcelable)
                })
            }
            dismiss()
        }
    }

    private fun observeViewModel() {
        viewModel.previousAddresses.observe(viewLifecycleOwner) { addresses ->
            if (addresses.isNullOrEmpty()) {
                binding.recyclerAddresses.visibility = View.GONE
                binding.textEmptyState.visibility = View.VISIBLE
            } else {
                binding.recyclerAddresses.visibility = View.VISIBLE
                binding.textEmptyState.visibility = View.GONE
                addressAdapter.submitList(addresses)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}