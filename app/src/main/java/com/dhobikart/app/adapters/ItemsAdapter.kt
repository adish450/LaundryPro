package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.R
import com.dhobikart.app.databinding.ItemLaundryItemBinding
import com.dhobikart.app.models.CartItem
import com.dhobikart.app.models.ServiceCloth
import java.text.NumberFormat
import java.util.Locale

class ItemsAdapter(
    private val cartItems: Map<String, CartItem>,
    private val onAddItem: (item: ServiceCloth) -> Unit,
    private val onRemoveItem: (item: ServiceCloth) -> Unit
) : ListAdapter<ServiceCloth, ItemsAdapter.ItemViewHolder>(ItemDiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLaundryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, cartItems[item.clothId], onAddItem, onRemoveItem)
    }

    class ItemViewHolder(private val binding: ItemLaundryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: ServiceCloth,
            cartItem: CartItem?,
            onAddItem: (item: ServiceCloth) -> Unit,
            onRemoveItem: (item: ServiceCloth) -> Unit
        ) {
            binding.itemName.text = item.name
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.itemPrice.text = "${format.format(item.price)}"
            binding.itemImage.setImageResource(R.drawable.item_placeholder)

            val quantity = cartItem?.quantity ?: 0

            if (quantity > 0) {
                binding.btnAdd.visibility = View.GONE
                binding.quantityStepper.visibility = View.VISIBLE
                binding.itemQuantity.text = quantity.toString()
            } else {
                binding.btnAdd.visibility = View.VISIBLE
                binding.quantityStepper.visibility = View.GONE
            }

            // --- CORRECTED CLICK LISTENERS ---
            // The adapter now calculates the new quantity before calling the callback.
            binding.btnAdd.setOnClickListener { onAddItem(item) }
            binding.btnIncrease.setOnClickListener { onAddItem(item) }
            binding.btnDecrease.setOnClickListener { onRemoveItem(item) }
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<ServiceCloth>() {
    override fun areItemsTheSame(oldItem: ServiceCloth, newItem: ServiceCloth): Boolean {
        return oldItem.clothId == newItem.clothId
    }
    override fun areContentsTheSame(oldItem: ServiceCloth, newItem: ServiceCloth): Boolean {
        return oldItem == newItem
    }
}