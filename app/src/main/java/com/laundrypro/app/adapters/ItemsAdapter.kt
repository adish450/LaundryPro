package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemLaundryItemBinding
import com.laundrypro.app.models.LaundryItem

class ItemsAdapter(
    private val onItemClick: (LaundryItem) -> Unit
) : RecyclerView.Adapter<ItemsAdapter.ItemViewHolder>() {

    private var items = listOf<LaundryItem>()

    fun updateItems(newItems: List<LaundryItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLaundryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ItemViewHolder(
        private val binding: ItemLaundryItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LaundryItem) {
            binding.apply {
                textItemName.text = item.name
                textItemPrice.text = "$${String.format("%.2f", item.price)}"

                btnAddToCart.setOnClickListener {
                    onItemClick(item)
                }
            }
        }
    }
}