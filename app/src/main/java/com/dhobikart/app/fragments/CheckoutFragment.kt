package com.dhobikart.app.fragments

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.R
import com.dhobikart.app.adapters.CalendarAdapter
import com.dhobikart.app.adapters.TimePickerAdapter
import com.dhobikart.app.databinding.FragmentCheckoutBinding
import com.dhobikart.app.viewmodels.LaundryViewModel
import java.text.NumberFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar
import kotlin.math.abs
import kotlin.math.pow

class CheckoutFragment : Fragment(R.layout.fragment_checkout) {

    private var _binding: FragmentCheckoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LaundryViewModel by activityViewModels()
    private lateinit var calendarAdapter: CalendarAdapter
    private lateinit var timePickerAdapter: TimePickerAdapter

    // Properties to hold selected values
    private var selectedDate: Date? = null
    private var selectedHour: String = "09" // Default to start of service hours
    private var selectedMinute: String = "00"
    private var selectedTimeSlotView: View? = null

    // Variables to track the last vibrated position for each picker
    private var lastVibratedHourPos = -1
    private var lastVibratedMinutePos = -1

    // Properties for order summary
    private var subtotal: Double = 0.0
    private var discount: Double = 0.0
    private var total: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            subtotal = it.getDouble(ARG_SUBTOTAL)
            discount = it.getDouble(ARG_DISCOUNT)
            total = it.getDouble(ARG_TOTAL)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCheckoutBinding.bind(view)

        setupToolbar()
        setupCalendar()
        setupTimePickers()
        populateOrderSummary()
        observeViewModel()
        setupClickListeners()

        // Fetch user data
        // This assumes your ViewModel loads the user from SessionManager on init
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupCalendar() {
        calendarAdapter = CalendarAdapter { date ->
            // Update the selected date when the user taps a day in the calendar
            selectedDate = date
            //Toast.makeText(context, "Date selected: $date", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerCalendar.adapter = calendarAdapter
        updateCalendarHeader() // Use helper to update header and button state

        binding.btnPrevMonth.setOnClickListener {
            if (!calendarAdapter.isAtCurrentMonth()) {
                calendarAdapter.prevMonth()
                updateCalendarHeader()
            }
        }

        binding.btnNextMonth.setOnClickListener {
            calendarAdapter.nextMonth()
            updateCalendarHeader()
        }
    }

    private fun setupTimePickers() {
        val hours = (0..23).map { "%02d".format(it) }
        setupPicker(binding.recyclerHourPicker, hours, 9, { selectedHour = it.toInt().toString() }) { lastVibratedHourPos = it }

        val minutes = (0..59).map { "%02d".format(it) }
        setupPicker(binding.recyclerMinutePicker, minutes, 0, { selectedMinute =
            it.toInt().toString()
        }) { lastVibratedMinutePos = it }
    }

    private fun setupPicker(recyclerView: RecyclerView,
                            data: List<String>, defaultPosition: Int,
                            onTimeSelected: (String) -> Unit,
                            updateLastVibratedPos: (Int) -> Unit) {
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = TimePickerAdapter(data)

        recyclerView.post {
            val padding = recyclerView.height / 2 - (resources.getDimensionPixelSize(R.dimen.time_picker_item_height) / 2)
            recyclerView.setPadding(0, padding, 0, padding)
            layoutManager.scrollToPositionWithOffset(defaultPosition, 0)
        }

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recyclerView)

        // Add a listener to handle scroll conflict and haptics
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                updatePickerItemAppearance(recyclerView, layoutManager)

                // Trigger vibration as the user scrolls
                val centerView = snapHelper.findSnapView(layoutManager)
                val pos = if (centerView != null) layoutManager.getPosition(centerView) else -1

                val lastPos = if (recyclerView.id == R.id.recycler_hour_picker) lastVibratedHourPos else lastVibratedMinutePos

                if (pos != -1 && pos != lastPos) {
                    vibrate()
                    updateLastVibratedPos(pos)
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    updatePickerItemAppearance(recyclerView, layoutManager)
                    val centerView = snapHelper.findSnapView(layoutManager)
                    val pos = layoutManager.getPosition(centerView!!)
                    if (pos != RecyclerView.NO_POSITION) {
                        onTimeSelected(data[pos])
                    }
                }
            }
        })

        // Add a listener to prevent the parent ScrollView from hijacking the scroll
        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                if (e.action == MotionEvent.ACTION_DOWN) {
                    binding.scrollView.requestDisallowInterceptTouchEvent(true)
                } else if (e.action == MotionEvent.ACTION_UP || e.action == MotionEvent.ACTION_CANCEL) {
                    binding.scrollView.requestDisallowInterceptTouchEvent(false)
                }
                return false
            }
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
        })
    }

    private fun updatePickerItemAppearance(recyclerView: RecyclerView, layoutManager: LinearLayoutManager) {
        val centerOfRecyclerView = recyclerView.height / 2f
        for (i in 0 until recyclerView.childCount) {
            val child = recyclerView.getChildAt(i)
            val childCenter = (layoutManager.getDecoratedTop(child) + layoutManager.getDecoratedBottom(child)) / 2f
            val distanceToCenter = abs(centerOfRecyclerView - childCenter)

            val scale = (1.0f - (distanceToCenter / centerOfRecyclerView).pow(2) * 0.25f).coerceIn(0.75f, 1.0f)
            val alpha = (1.0f - (distanceToCenter / centerOfRecyclerView).pow(2) * 0.75f).coerceIn(0.25f, 1.0f)

            child.scaleX = scale
            child.scaleY = scale
            child.alpha = alpha
        }
    }

    private fun updateCalendarHeader() {
        binding.tvMonthYear.text = calendarAdapter.getMonthYear()
        // Disable the back button if we are at the current month
        if (calendarAdapter.isAtCurrentMonth()) {
            binding.btnPrevMonth.isEnabled = false
            binding.btnPrevMonth.alpha = 0.5f
        } else {
            binding.btnPrevMonth.isEnabled = true
            binding.btnPrevMonth.alpha = 1.0f
        }
    }

    private fun selectTimeSlotView(view: View) {
        // Deselect the previously selected time slot
        selectedTimeSlotView?.isSelected = false
        // Select the new one
        view.isSelected = true
        selectedTimeSlotView = view
    }

    private fun populateOrderSummary() {
        val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
        binding.tvSubtotal.text = format.format(subtotal)

        if (discount > 0) {
            binding.layoutDiscount.visibility = View.VISIBLE
            binding.tvDiscountAmount.text = "-${format.format(discount)}"
        } else {
            binding.layoutDiscount.visibility = View.GONE
        }

        // Using a fixed value for delivery charges as an example
        val deliveryCharges = 5.00
        binding.tvDeliveryCharges.text = format.format(deliveryCharges)
        binding.tvTotal.text = format.format(total)
    }

    private fun observeViewModel() {
        viewModel.currentUser.observe(viewLifecycleOwner) { user ->
            // Display the first address as the selected one
            user?.address?.firstOrNull()?.let { address ->
                binding.tvAddress.text = "${address.street}, ${address.city}, ${address.state} ${address.zip}"
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnChangeAddress.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ManageAddressesFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnChangePayment.setOnClickListener {
            // TODO: Navigate to a payment selection screen
            Toast.makeText(context, "Change Payment Clicked", Toast.LENGTH_SHORT).show()
        }

        binding.btnPlaceOrder.setOnClickListener {
            if (isTimeInServiceHours()) {
                Toast.makeText(context, "Order Placed!", Toast.LENGTH_SHORT).show()
                // TODO: Implement actual order placement logic
            } else {
                Toast.makeText(context, "Please select a time between 09:00 and 18:00.", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun isTimeInServiceHours(): Boolean {
        // Service hours are 09:00 to 18:00
        return selectedHour >= 9.toString() && (selectedHour < 18.toString() || (selectedHour == 18.toString() && selectedMinute == 0.toString()))
    }

    private fun vibrate() {
        val vibrator = context?.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Use the predefined TICK effect for a crisp, clean haptic feedback
            vibrator?.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_HEAVY_CLICK))
        } else {
            // Fallback for older APIs
            @Suppress("DEPRECATION")
            vibrator?.vibrate(50)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_SUBTOTAL = "subtotal"
        private const val ARG_DISCOUNT = "discount"
        private const val ARG_TOTAL = "total"

        @JvmStatic
        fun newInstance(subtotal: Double, discount: Double, total: Double) =
            CheckoutFragment().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_SUBTOTAL, subtotal)
                    putDouble(ARG_DISCOUNT, discount)
                    putDouble(ARG_TOTAL, total)
                }
            }
    }
}

