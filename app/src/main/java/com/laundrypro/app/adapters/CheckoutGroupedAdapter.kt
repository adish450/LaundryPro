package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemCheckoutGroupBinding
import com.laundrypro.app.models.GroupedCartItems

class CheckoutGroupedAdapter : ListAdapter<GroupedCartItems, CheckoutGroupedAdapter.GroupViewHolder>(GroupDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupViewHolder {
        val binding = ItemCheckoutGroupBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GroupViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GroupViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class GroupViewHolder(private val binding: ItemCheckoutGroupBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(groupedItems: GroupedCartItems) {
            binding.textServiceNameHeader.text = groupedItems.serviceName

            // Setup the nested RecyclerView for the clothes in this service group
            val itemsAdapter = CheckoutSummaryItemAdapter(groupedItems.items)
            binding.recyclerNestedItems.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerNestedItems.adapter = itemsAdapter
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
