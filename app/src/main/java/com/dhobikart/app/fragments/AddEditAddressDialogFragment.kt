package com.dhobikart.app.fragments

import android.Manifest
import android.app.Dialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.dhobikart.app.databinding.DialogAddEditAddressBinding
import com.dhobikart.app.models.Address
import com.dhobikart.app.viewmodels.LaundryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddEditAddressDialogFragment : DialogFragment() {

    private val viewModel: LaundryViewModel by activityViewModels()
    private var addressToEdit: Address? = null
    private lateinit var binding: DialogAddEditAddressBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

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
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DialogAddEditAddressBinding.inflate(layoutInflater)

        addressToEdit?.let {
            binding.etStreet.setText(it.street)
            binding.etCity.setText(it.city)
            binding.etState.setText(it.state)
            binding.etZip.setText(it.zip)
        }

        binding.btnUseGps.setOnClickListener {
            checkPermissionsAndFetchLocation()
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

    private fun checkPermissionsAndFetchLocation() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(requireContext(), "Location permission is needed to use GPS.", Toast.LENGTH_LONG).show()
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                reverseGeocodeLocation(location)
            } else {
                Toast.makeText(requireContext(), "Could not retrieve location. Please ensure GPS is enabled.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun reverseGeocodeLocation(location: android.location.Location) {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val geocodedAddress = addresses?.firstOrNull()

                withContext(Dispatchers.Main) {
                    if (geocodedAddress != null) {
                        binding.etStreet.setText(geocodedAddress.thoroughfare)
                        binding.etCity.setText(geocodedAddress.locality)
                        binding.etState.setText(geocodedAddress.adminArea)
                        binding.etZip.setText(geocodedAddress.postalCode)
                    } else {
                        Toast.makeText(requireContext(), "Address not found for this location.", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error fetching address.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
