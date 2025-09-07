package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemCartDetailsBinding
import com.dhobikart.app.databinding.ItemCartGroupBinding
import com.dhobikart.app.databinding.ItemCartGroupFooterBinding
import com.dhobikart.app.databinding.ItemCartGroupHeaderBinding
import com.dhobikart.app.models.CartDisplayItem
import com.dhobikart.app.models.CartItem
import java.text.NumberFormat
import java.util.Locale

private const val VIEW_TYPE_HEADER = 0
private const val VIEW_TYPE_ITEM = 1
private const val VIEW_TYPE_FOOTER = 2

class GroupedCartAdapter(
    private val onUpdateQuantity: (item: CartItem, newQuantity: Int) -> Unit,
    private val onDeleteItem: (item: CartItem) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val displayList = mutableListOf<CartDisplayItem>()

    fun submitList(list: List<CartDisplayItem>) {
        displayList.clear()
        displayList.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (displayList[position]) {
            is CartDisplayItem.Header -> VIEW_TYPE_HEADER
            is CartDisplayItem.Item -> VIEW_TYPE_ITEM
            is CartDisplayItem.Footer -> VIEW_TYPE_FOOTER
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_HEADER -> {
                val binding = ItemCartGroupHeaderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                HeaderViewHolder(binding)
            }
            VIEW_TYPE_ITEM -> {
                val binding = ItemCartDetailsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ItemViewHolder(binding)
            }
            VIEW_TYPE_FOOTER -> {
                val binding = ItemCartGroupFooterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                FooterViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = displayList[position]) {
            is CartDisplayItem.Header -> (holder as HeaderViewHolder).bind(item)
            is CartDisplayItem.Item -> (holder as ItemViewHolder).bind(item, onUpdateQuantity, onDeleteItem)
            is CartDisplayItem.Footer -> (holder as FooterViewHolder).bind(item)
        }
    }

    override fun getItemCount(): Int = displayList.size

    class HeaderViewHolder(private val binding: ItemCartGroupHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(header: CartDisplayItem.Header) {
            binding.tvServiceName.text = header.serviceName
        }
    }

    class FooterViewHolder(private val binding: ItemCartGroupFooterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(footer: CartDisplayItem.Footer) {
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))
            binding.tvGroupSubtotal.text = format.format(footer.subtotal)
        }
    }

    class ItemViewHolder(private val binding: ItemCartDetailsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartDisplayItem: CartDisplayItem.Item,
                 onUpdateQuantity: (item: CartItem, newQuantity: Int) -> Unit,
                 onDeleteItem: (item: CartItem) -> Unit) {
            val cartItem = cartDisplayItem.cartItem
            val format = NumberFormat.getCurrencyInstance(Locale("en", "IN"))

            binding.itemName.text = cartItem.name
            binding.itemPricePerUnit.text = "${cartItem.price} / item"
            binding.itemQuantity.text = cartItem.quantity.toString()
            binding.itemTotalPrice.text = format.format((cartItem.price) * cartItem.quantity)

            binding.btnIncrease.setOnClickListener {
                onUpdateQuantity(
                    cartItem,
                    cartItem.quantity + 1
                )
            }
            binding.btnDecrease.setOnClickListener {
                onUpdateQuantity(
                    cartItem,
                    cartItem.quantity - 1
                )
            }
            binding.btnDeleteItem.setOnClickListener { onDeleteItem(cartItem) }
        }
    }
}