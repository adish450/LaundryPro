package com.laundrypro.app

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.laundrypro.app.adapters.CheckoutItemsAdapter
import com.laundrypro.app.databinding.ActivityCheckoutBinding
import com.laundrypro.app.viewmodels.LaundryViewModel
import java.text.SimpleDateFormat
import java.util.*

class CheckoutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: LaundryViewModel by viewModels()
    private var selectedDate: Calendar = Calendar.getInstance()
    private var selectedTime: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Confirm Order"
    }

    private fun observeViewModel() {
        // Observe cart items to display them
        viewModel.cartItems.observe(this) { items ->
            if (items.isNotEmpty()) {
                val adapter = CheckoutItemsAdapter(items)
                binding.recyclerOrderItems.layoutManager = LinearLayoutManager(this)
                binding.recyclerOrderItems.adapter = adapter
            }
        }

        // Observe user to populate address spinner
        viewModel.currentUser.observe(this) { user ->
            user?.let {
                val addresses = it.addresses?.map { address -> address.fullAddress } ?: emptyList()
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, addresses)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerAddress.adapter = adapter
            }
        }

        // Observe cart items and applied offer to update pricing summary
        viewModel.cartItems.observe(this) { updatePricing() }
        viewModel.appliedOffer.observe(this) { updatePricing() }
    }

    private fun setupClickListeners() {
        binding.btnSelectDate.setOnClickListener { showDatePicker() }
        binding.btnSelectTime.setOnClickListener { showTimePicker() }

        binding.btnPlaceOrder.setOnClickListener {
            if (binding.spinnerAddress.selectedItem == null) {
                Toast.makeText(this, "Please select a pickup address", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (binding.textSelectedDate.text.isBlank() || binding.textSelectedTime.text.isBlank()) {
                Toast.makeText(this, "Please select a pickup date and time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val address = binding.spinnerAddress.selectedItem.toString()
            val pickupDateTime = "${binding.textSelectedDate.text} at ${binding.textSelectedTime.text}"

            viewModel.placeOrder(address, pickupDateTime) { success, message ->
                if (success) {
                    Toast.makeText(this, "Order placed successfully! ID: $message", Toast.LENGTH_LONG).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to place order: $message", Toast.LENGTH_SHORT).show()
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
        DatePickerDialog(this, dateSetListener,
            selectedDate.get(Calendar.YEAR),
            selectedDate.get(Calendar.MONTH),
            selectedDate.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
            selectedTime.set(Calendar.MINUTE, minute)
            val format = SimpleDateFormat("hh:mm a", Locale.US)
            binding.textSelectedTime.text = format.format(selectedTime.time)
        }
        TimePickerDialog(this, timeSetListener,
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}