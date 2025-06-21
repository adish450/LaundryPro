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
import com.laundrypro.app.adapters.CheckoutItemsAdapter
import com.laundrypro.app.databinding.FragmentCheckoutBinding
import com.laundrypro.app.viewmodels.LaundryViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CheckoutFragment : Fragment() {

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
            // Handle back navigation
            parentFragmentManager.popBackStack()
        }
    }

    private fun observeViewModel() {
        viewModel.cartItems.observe(viewLifecycleOwner) { items ->
            if (!items.isNullOrEmpty()) {
                binding.recyclerOrderItems.adapter = CheckoutItemsAdapter(items)
                binding.recyclerOrderItems.layoutManager = LinearLayoutManager(context)
            }
        }

        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            user?.addresses?.let {
                val addresses = it.map { address -> address.fullAddress }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, addresses)
                binding.spinnerAddress.adapter = adapter
            }
        }

        // Observe cart items and applied offer to update pricing summary
        activity?.let { viewModel.cartItems.observe(it) { updatePricing() } }
        activity?.let { viewModel.appliedOffer.observe(it) { updatePricing() } }
    }

    private fun setupClickListeners() {
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }

        binding.btnPlaceOrder.setOnClickListener {
            if (binding.spinnerAddress.selectedItem == null) {
                Toast.makeText(context, "Please select a pickup address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.textSelectedDate.text.isBlank() || binding.textSelectedTime.text.isBlank()) {
                Toast.makeText(context, "Please select a pickup date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val address = binding.spinnerAddress.selectedItem.toString()
            val pickupDateTime = "${binding.textSelectedDate.text} at ${binding.textSelectedTime.text}"

            viewModel.placeOrder(address, pickupDateTime) { success, message ->
                if (success) {
                    Toast.makeText(context, "Order placed successfully! ID: $message", Toast.LENGTH_LONG).show()
                    //add logic to place order
                } else {
                    Toast.makeText(context, "Failed to place order: $message", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            selectedDate.set(Calendar.YEAR, year)
            selectedDate.set(Calendar.MONTH, month)
            selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val format = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            binding.textSelectedDate.text = format.format(selectedDate.time)
        }
        context?.let {
            DatePickerDialog(
                it, dateSetListener,
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)).show()
        }
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            val format = SimpleDateFormat("hh:mm a", Locale.US)
            binding.textSelectedTime.text = format.format(selectedTime.time)
        }
        TimePickerDialog(activity, timeSetListener,
            selectedTime.get(Calendar.HOUR_OF_DAY),
            selectedTime.get(Calendar.MINUTE),
            false).show()
    }

    private fun updatePricing() {
        val summary = viewModel.calculateTotal()
        binding.textSubtotal.text = "$${String.format("%.2f", summary.subtotal)}"
        binding.textDiscount.text = "-$${String.format("%.2f", summary.discount)}"
        binding.textTotal.text = "$${String.format("%.2f", summary.total)}"

        binding.discountRow.visibility = if (summary.discount > 0) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}