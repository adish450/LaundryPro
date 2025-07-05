package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemCartBinding
import com.laundrypro.app.models.CartItem

class CartAdapter(
    private val onQuantityChanged: (itemId: String, serviceId: String, newQuantity: Int) -> Unit,
    private val onItemRemoved: (itemId: String, serviceId: String) -> Unit
) : ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = getItem(position)
        holder.bind(cartItem, onQuantityChanged, onItemRemoved)
    }

    class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cartItem: CartItem,
            onQuantityChanged: (itemId: String, serviceId: String, newQuantity: Int) -> Unit,
            onItemRemoved: (itemId: String, serviceId: String) -> Unit
        ) {
            binding.textItemName.text = cartItem.name
            binding.textItemPrice.text = String.format("₹%.2f each", cartItem.price)
            binding.textQuantity.text = cartItem.quantity.toString()
            binding.textTotalPrice.text = String.format("₹%.2f", cartItem.price * cartItem.quantity)

            binding.btnIncrease.setOnClickListener {
                onQuantityChanged(cartItem.itemId, cartItem.serviceId, cartItem.quantity + 1)
            }
            binding.btnDecrease.setOnClickListener {
                if (cartItem.quantity > 0) {
                    onQuantityChanged(cartItem.itemId, cartItem.serviceId, cartItem.quantity - 1)
                }
            }
            binding.btnRemove.setOnClickListener {
                onItemRemoved(cartItem.itemId, cartItem.serviceId)
            }
        }
    }

    class CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
        override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem.itemId == newItem.itemId && oldItem.serviceId == newItem.serviceId
        }

        override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
            return oldItem == newItem
        }
    }
}