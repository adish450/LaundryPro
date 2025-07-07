package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemGroupedCartBinding
import com.laundrypro.app.models.GroupedCartItems

class GroupedCartAdapter(
    private val onQuantityChanged: (itemId: String, serviceId: String, newQuantity: Int) -> Unit,
    private val onItemRemoved: (itemId: String, serviceId: String) -> Unit
) : ListAdapter<GroupedCartItems, GroupedCartAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemGroupedCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding, onQuantityChanged, onItemRemoved)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GroupViewHolder(
        private val binding: ItemGroupedCartBinding,
        private val onQuantityChanged: (itemId: String, serviceId: String, newQuantity: Int) -> Unit,
        private val onItemRemoved: (itemId: String, serviceId: String) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupedItems: GroupedCartItems) {
            binding.textServiceNameHeader.text = groupedItems.serviceName

            // Setup the nested RecyclerView
            val itemsAdapter = CartAdapter(onQuantityChanged, onItemRemoved)
            binding.recyclerNestedItems.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerNestedItems.adapter = itemsAdapter
            itemsAdapter.submitList(groupedItems.items)
        }
    }

    class GroupDiffCallback : DiffUtil.ItemCallback<GroupedCartItems>() {
        override fun areItemsTheSame(oldItem: GroupedCartItems, newItem: GroupedCartItems): Boolean {
            return oldItem.serviceName == newItem.serviceName
        }
        override fun areContentsTheSame(oldItem: GroupedCartItems, newItem: GroupedCartItems): Boolean {
            return oldItem == newItem
        }
    }
}
