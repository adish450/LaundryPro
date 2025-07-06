package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemLaundryItemBinding
import com.laundrypro.app.models.LaundryItem
import com.laundrypro.app.models.ServiceCloth

class ItemsAdapter(private val onAddItemClicked: (ServiceCloth) -> Unit) :
    ListAdapter<ServiceCloth, ItemsAdapter.ItemViewHolder>(ServiceClothDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemLaundryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(getItem(position), onAddItemClicked)
    }

    class ItemViewHolder(private val binding: ItemLaundryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ServiceCloth, onAddItemClicked: (ServiceCloth) -> Unit) {
            binding.textItemName.text = item.name
            binding.textItemPrice.text = String.format("₹%.2f", item.price)
            binding.btnAddToCart.setOnClickListener { onAddItemClicked(item) }
        }
    }

    class ServiceClothDiffCallback : DiffUtil.ItemCallback<ServiceCloth>() {
        override fun areItemsTheSame(oldItem: ServiceCloth, newItem: ServiceCloth): Boolean {
            return oldItem.clothId == newItem.clothId
        }
        override fun areContentsTheSame(oldItem: ServiceCloth, newItem: ServiceCloth): Boolean {
            return oldItem == newItem
        }
    }
}