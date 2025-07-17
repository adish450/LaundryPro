package com.dhobikart.app.fragments

import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.dhobikart.app.databinding.DialogAddEditAddressBinding
import com.dhobikart.app.models.Address
import com.dhobikart.app.viewmodels.LaundryViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddEditAddressDialogFragment : DialogFragment() {

    private val viewModel: LaundryViewModel by activityViewModels()
    private var addressToEdit: Address? = null

    companion object {
        private const val ARG_ADDRESS = "address_to_edit"
        fun newInstance(address: Address? = null): AddEditAddressDialogFragment {
            val fragment = AddEditAddressDialogFragment()
            address?.let {
                fragment.arguments = Bundle().apply { putParcelable(ARG_ADDRESS, it) }
            }
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            addressToEdit = it.getParcelable(ARG_ADDRESS)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val binding = DialogAddEditAddressBinding.inflate(layoutInflater)

        addressToEdit?.let {
            binding.etStreet.setText(it.street)
            binding.etCity.setText(it.city)
            binding.etState.setText(it.state)
            binding.etZip.setText(it.zip)
        }

        return MaterialAlertDialogBuilder(requireContext())
            .setTitle(if (addressToEdit == null) "Add New Address" else "Edit Address")
            .setView(binding.root)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Save") { _, _ ->
                val street = binding.etStreet.text.toString().trim()
                val city = binding.etCity.text.toString().trim()
                val state = binding.etState.text.toString().trim()
                val zip = binding.etZip.text.toString().trim()

                if (street.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && zip.isNotEmpty()) {
                    val newAddress = Address(street, city, state, zip)
                    if (addressToEdit == null) {
                        viewModel.addAddress(newAddress)
                    } else {
                        viewModel.updateAddress(addressToEdit!!, newAddress)
                    }
                } else {
                    Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .create()
    }
}