package com.laundrypro.app.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.R
import com.laundrypro.app.adapters.CheckoutItemsAdapter
import com.laundrypro.app.databinding.FragmentCheckoutBinding
import com.laundrypro.app.viewmodels.LaundryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCheckoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }

        binding.btnPlaceOrder.setOnClickListener {
            if (binding.spinnerAddress.selectedItem == null) {
                Toast.makeText(context, "Please select an address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.textSelectedDate.text.isBlank() || binding.textSelectedTime.text.isBlank()) {
                Toast.makeText(context, "Please select date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val address = binding.spinnerAddress.selectedItem.toString()
            val pickupDateTime = "${binding.textSelectedDate.text} at ${binding.textSelectedTime.text}"

            viewModel.placeOrder(address, pickupDateTime) { success, message ->
                if (success) {
                    Toast.makeText(context, "Order placed successfully!", Toast.LENGTH_LONG).show()
                    // Navigate back to the previous screen (likely the cart or home)
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(context, "Failed to place order: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun observeViewModel() {
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

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.addresses?.let { addressesList ->
                val addresses = addressesList.map { it.fullAddress }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addresses)
                binding.spinnerAddress.adapter = adapter
            }
        }
    }

    private fun updatePricing() {
        // This check is crucial to prevent crashes if the view is destroyed
        // while an observer is still trying to fire.
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
        _binding = null // Important to prevent memory leaks
    }
}
