package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemCheckoutSummaryItemBinding
import com.dhobikart.app.models.CartItem

class CheckoutSummaryItemAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<CheckoutSummaryItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemCheckoutSummaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ItemViewHolder(private val binding: ItemCheckoutSummaryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            binding.textItemSummary.text = "${item.quantity} x ${item.name}"
            binding.textItemPrice.text = String.format("₹%.2f", item.price * item.quantity)
        }
    }
}
