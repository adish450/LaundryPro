package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemCheckoutBinding
import com.dhobikart.app.models.CartItem

class CheckoutItemsAdapter(private val items: List<CartItem>) : RecyclerView.Adapter<CheckoutItemsAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        // Correctly inflates using the generated ItemCheckoutBinding class
        val binding = ItemCheckoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    class ItemViewHolder(private val binding: ItemCheckoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CartItem) {
            // Uses the correct view IDs from the updated layout
            binding.textItemNameSummary.text = "${item.quantity} x ${item.name}"
            binding.textItemTotalPrice.text = "₹${String.format("%.2f", item.price * item.quantity)}"
        }
    }
}