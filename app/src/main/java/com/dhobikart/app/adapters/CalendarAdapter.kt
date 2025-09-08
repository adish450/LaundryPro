package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.R
import com.dhobikart.app.databinding.ItemCalendarDayBinding
import java.util.Calendar
import java.util.Date

class CalendarAdapter(
    private val onDateSelected: (Date) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    private val dates = mutableListOf<Date?>()
    private val calendar = Calendar.getInstance()
    private val today = Calendar.getInstance()
    private var selectedDate: Date = calendar.time

    init {
        updateCalendar()
    }

    fun isAtCurrentMonth(): Boolean {
        return calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == today.get(Calendar.MONTH)
    }

    private fun updateCalendar() {
        dates.clear()
        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val firstDayOfMonth = monthCalendar.get(Calendar.DAY_OF_WEEK) - 1
        monthCalendar.add(Calendar.DAY_OF_MONTH, -firstDayOfMonth)

        while (dates.size < 42) {
            dates.add(monthCalendar.time)
            monthCalendar.add(Calendar.DAY_OF_MONTH, 1)
        }
        notifyDataSetChanged()
    }

    fun nextMonth() {
        calendar.add(Calendar.MONTH, 1)
        updateCalendar()
    }

    fun prevMonth() {
        calendar.add(Calendar.MONTH, -1)
        updateCalendar()
    }

    fun getMonthYear(): String {
        val sdf = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
        return sdf.format(calendar.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        dates[position]?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = dates.size

    inner class ViewHolder(private val binding: ItemCalendarDayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(date: Date) {
            val dateCal = Calendar.getInstance().apply { time = date }
            val todayCal = Calendar.getInstance()

            binding.tvDayText.text = dateCal.get(Calendar.DAY_OF_MONTH).toString()

            val isSameMonth = dateCal.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
            val isToday = areDatesSameDay(date, todayCal.time)
            val isSelected = areDatesSameDay(date, selectedDate)

            val context = itemView.context

            if (isSameMonth) {
                binding.tvDayText.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface))
                itemView.isClickable = true
                itemView.setOnClickListener {
                    selectedDate = date
                    onDateSelected(date)
                    notifyDataSetChanged()
                }

                when {
                    isSelected -> {
                        binding.tvDayText.background = ContextCompat.getDrawable(context, R.drawable.bg_calendar_date_selected)
                        binding.tvDayText.setTextColor(ContextCompat.getColor(context, R.color.colorOnPrimary))
                    }
                    isToday -> {
                        binding.tvDayText.background = ContextCompat.getDrawable(context, R.drawable.bg_calendar_date_today)
                        binding.tvDayText.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    }
                    else -> {
                        binding.tvDayText.background = null
                        binding.tvDayText.setTextColor(ContextCompat.getColor(context, R.color.colorOnSurface))
                    }
                }
            } else {
                binding.tvDayText.setTextColor(ContextCompat.getColor(context, R.color.icon_tint_unselected))
                itemView.isClickable = false
                binding.tvDayText.background = null
            }
        }

        private fun areDatesSameDay(date1: Date, date2: Date): Boolean {
            val cal1 = Calendar.getInstance().apply { time = date1 }
            val cal2 = Calendar.getInstance().apply { time = date2 }
            return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }
    }
}