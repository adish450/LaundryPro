package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemAdminOrderBinding // You will need to create this layout
import com.laundrypro.app.models.AdminOrder

class AdminOrdersAdapter : ListAdapter<AdminOrder, AdminOrdersAdapter.OrderViewHolder>(AdminOrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(private val binding: ItemAdminOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: AdminOrder) {
            binding.textCustomerName.text = order.userId.name
            binding.textOrderId.text = "Order #${order.id.takeLast(6)}"
            binding.textOrderStatus.text = order.status
            binding.textOrderTotal.text = String.format("₹%.2f", order.totalAmount)
            binding.textPickupAddress.text = order.pickupAddress?.toDisplayString() ?: "No address"
        }
    }

    class AdminOrderDiffCallback : DiffUtil.ItemCallback<AdminOrder>() {
        override fun areItemsTheSame(oldItem: AdminOrder, newItem: AdminOrder): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AdminOrder, newItem: AdminOrder): Boolean = oldItem == newItem
    }
}