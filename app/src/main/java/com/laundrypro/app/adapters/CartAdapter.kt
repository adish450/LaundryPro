package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemCartBinding
import com.laundrypro.app.models.CartItem

class CartAdapter(
    private val onQuantityChanged: (String, Int, Int) -> Unit,
    private val onItemRemoved: (String, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private var cartItems: List<CartItem> = emptyList()

    fun updateCart(newCartItems: List<CartItem>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(cartItems[position], onQuantityChanged, onItemRemoved)
    }

    override fun getItemCount() = cartItems.size

    class CartViewHolder(private val binding: ItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(
            cartItem: CartItem,
            onQuantityChanged: (String, Int, Int) -> Unit,
            onItemRemoved: (String, Int) -> Unit
        ) {
            binding.textItemName.text = cartItem.name
            binding.textItemPrice.text = "$${String.format("%.2f", cartItem.price)} each"
            binding.textQuantity.text = cartItem.quantity.toString()
            binding.textTotalPrice.text = "$${String.format("%.2f", cartItem.price * cartItem.quantity)}"

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
}