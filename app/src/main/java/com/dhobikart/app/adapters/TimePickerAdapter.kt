package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemTimePickerBinding

class TimePickerAdapter(
    private val timeSlots: List<String>
) : RecyclerView.Adapter<TimePickerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTimePickerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(timeSlots[position])
    }

    override fun getItemCount(): Int = timeSlots.size

    class ViewHolder(private val binding: ItemTimePickerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(time: String) {
            binding.tvTime.text = time
        }
    }
}