package com.dhobikart.app.fragments

import android.Manifest
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dhobikart.app.MainActivity
import com.dhobikart.app.R
import com.dhobikart.app.adapters.CheckoutGroupedAdapter
import com.dhobikart.app.adapters.SelectableAddressAdapter
import com.dhobikart.app.databinding.FragmentCheckoutBinding
import com.dhobikart.app.models.Address
import com.dhobikart.app.models.PlaceOrderResult
import com.dhobikart.app.viewmodels.LaundryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CheckoutFragment : Fragment() {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

    private lateinit var checkoutAdapter: CheckoutGroupedAdapter
    private lateinit var addressAdapter: SelectableAddressAdapter
    private var selectedPickupAddress: Address? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(requireContext(), "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        viewModel.onOrderPlacementHandled()

        setupToolbar()
        setupRecyclerViews()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupRecyclerViews() {
        checkoutAdapter = CheckoutGroupedAdapter()
        binding.recyclerOrderItems.layoutManager = LinearLayoutManager(context)
        binding.recyclerOrderItems.adapter = checkoutAdapter

        addressAdapter = SelectableAddressAdapter { address ->
            selectedPickupAddress = address
        }
        binding.recyclerSavedAddresses.layoutManager = LinearLayoutManager(context)
        binding.recyclerSavedAddresses.adapter = addressAdapter
    }

    private fun setupClickListeners() {
        binding.btnAddNewAddress.setOnClickListener {
            toggleNewAddressForm(true)
        }

        binding.btnCancelAddAddress.setOnClickListener {
            toggleNewAddressForm(false)
            clearAddressForm()
        }

        binding.btnSaveAddress.setOnClickListener {
            val street = binding.etStreet.text.toString().trim()
            val city = binding.etCity.text.toString().trim()
            val state = binding.etState.text.toString().trim()
            val zip = binding.etZip.text.toString().trim()

            if (street.isNotEmpty() && city.isNotEmpty() && state.isNotEmpty() && zip.isNotEmpty()) {
                val newAddress = Address(street, city, state, zip)
                viewModel.addAddress(newAddress)
                toggleNewAddressForm(false)
                clearAddressForm()
            } else {
                Toast.makeText(context, "Please fill all address fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (selectedPickupAddress == null) {
                Toast.makeText(context, "Please select or add a pickup address.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            viewModel.placeOrder(selectedPickupAddress!!)
        }

        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }
        binding.btnUseGps.setOnClickListener {
            checkPermissionsAndFetchLocation()
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val addresses = user?.address ?: emptyList()
            addressAdapter.submitList(addresses)
            if (addresses.isNotEmpty()) {
                selectedPickupAddress = addresses.first()
                addressAdapter.setSelectedAddress(addresses.first())
            }
        }

        viewModel.addressListUpdated.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { updatedUser ->
                val newAddress = updatedUser.address?.lastOrNull()
                if (newAddress != null) {
                    selectedPickupAddress = newAddress
                    addressAdapter.setSelectedAddress(newAddress)
                }
            }
        }

        viewModel.groupedCartItems.observe(viewLifecycleOwner) { groupedItems ->
            checkoutAdapter.submitList(groupedItems)
        }

        viewModel.placeOrderResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                is PlaceOrderResult.Loading -> {
                    binding.btnPlaceOrder.isEnabled = false
                    binding.btnPlaceOrder.text = "Placing Order..."
                }
                is PlaceOrderResult.Success -> {
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, OrderSuccessFragment.newInstance(result.order.id))
                        .commit()
                }
                is PlaceOrderResult.Error -> {
                    binding.btnPlaceOrder.isEnabled = true
                    binding.btnPlaceOrder.text = "Place Order"
                    Toast.makeText(context, "Failed to place order: ${result.message}", Toast.LENGTH_LONG).show()
                }
                is PlaceOrderResult.Idle -> {
                    binding.btnPlaceOrder.isEnabled = true
                    binding.btnPlaceOrder.text = "Place Order"
                }
            }
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { updatePricing() }
        viewModel.appliedOffer.observe(viewLifecycleOwner) { updatePricing() }
    }

    private fun toggleNewAddressForm(show: Boolean) {
        binding.newAddressForm.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnAddNewAddress.visibility = if (show) View.GONE else View.VISIBLE
    }

    private fun clearAddressForm() {
        binding.etStreet.text?.clear()
        binding.etCity.text?.clear()
        binding.etState.text?.clear()
        binding.etZip.text?.clear()
    }

    private fun updatePricing() {
        if (_binding == null) return
        val summary = viewModel.calculateTotal()
        binding.textSubtotal.text = String.format("₹%.2f", summary.subtotal)
        binding.textDiscount.text = String.format("-₹%.2f", summary.discount)
        binding.textTotal.text = String.format("₹%.2f", summary.total)
        binding.discountRow.visibility = if (summary.discount > 0) View.VISIBLE else View.GONE
    }

    private fun showDatePicker() {
        val listener = DatePickerDialog.OnDateSetListener { _, year, month, day ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, day)
            binding.textSelectedDate.text = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedDate.time)
        }
        DatePickerDialog(requireContext(), listener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val listener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hour)
            selectedTime.set(Calendar.MINUTE, minute)
            binding.textSelectedTime.text = SimpleDateFormat("hh:mm a", Locale.US).format(selectedTime.time)
        }
        TimePickerDialog(requireContext(), listener,
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            false).show()
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
