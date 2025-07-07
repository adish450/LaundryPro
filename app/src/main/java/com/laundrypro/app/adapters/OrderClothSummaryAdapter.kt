package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemOrderClothSummaryBinding
import com.laundrypro.app.models.PopulatedClothOrderItem

class OrderClothSummaryAdapter(private val items: List<PopulatedClothOrderItem>) :
    RecyclerView.Adapter<OrderClothSummaryAdapter.ClothViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ClothViewHolder {
        val binding = ItemOrderClothSummaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ClothViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ClothViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ClothViewHolder(private val binding: ItemOrderClothSummaryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PopulatedClothOrderItem) {
            binding.textClothSummary.text = "${item.quantity} x ${item.clothId.name}"
            binding.textClothTotal.text = String.format("₹%.2f", item.total)
        }
    }
}