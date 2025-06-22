package com.laundrypro.app.fragments

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
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.laundrypro.app.R
import com.laundrypro.app.adapters.CheckoutItemsAdapter
import com.laundrypro.app.databinding.FragmentCheckoutBinding
import com.laundrypro.app.viewmodels.LaundryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()

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

        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupClickListeners() {
        binding.btnUseGps.setOnClickListener {
            checkPermissionsAndFetchLocation()
        }
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }

        binding.btnPlaceOrder.setOnClickListener {
            val address = binding.etPickupAddress.text.toString() // Updated to get text from EditText
            if (address.isBlank() || address == "No default address found.") {
                Toast.makeText(context, "Please select a pickup address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.textSelectedDate.text.isBlank() || binding.textSelectedTime.text.isBlank()) {
                Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val pickupDateTime = "${binding.textSelectedDate.text} at ${binding.textSelectedTime.text}"

            viewModel.placeOrder(address, pickupDateTime) { success, message ->
                if (success) {
                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to place order: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            val defaultAddress = user?.addresses?.find { it.isDefault }?.fullAddress
            binding.etPickupAddress.setText(defaultAddress ?: "No default address found.") // Updated to set text in EditText
        }

        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (!items.isNullOrEmpty()) {
                binding.recyclerOrderItems.adapter = CheckoutItemsAdapter(items)
                binding.recyclerOrderItems.layoutManager = LinearLayoutManager(context)
            }
            updatePricing()
        }

        viewModel.appliedOffer.observe(viewLifecycleOwner) {
            updatePricing()
        }
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
        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
            try {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val addressText = addresses?.firstOrNull()?.getAddressLine(0) ?: "Address not found"

                withContext(Dispatchers.Main) {
                    binding.etPickupAddress.setText(addressText) // Updated to set text in EditText
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error fetching address.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun updatePricing() {
        if (_binding == null) return

        val summary = viewModel.calculateTotal()
        binding.textSubtotal.text = String.format("$%.2f", summary.subtotal)
        binding.textDiscount.text = String.format("-$%.2f", summary.discount)
        binding.textTotal.text = String.format("$%.2f", summary.total)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}