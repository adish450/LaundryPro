package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemAdminOrderBinding
import com.dhobikart.app.models.AdminOrder
import java.text.SimpleDateFormat
import java.util.*

class AdminOrdersAdapter(private val onOrderClicked: (AdminOrder) -> Unit) :
    ListAdapter<AdminOrder, AdminOrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemAdminOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position), onOrderClicked)
    }

    class OrderViewHolder(private val binding: ItemAdminOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: AdminOrder, onOrderClicked: (AdminOrder) -> Unit) {
            binding.textOrderId.text = "Order #${order.id.takeLast(6)}"
            binding.textOrderStatus.text = "Status: ${order.status}"
            binding.textOrderTotal.text = String.format("₹%.2f", order.totalAmount)
            binding.textCustomerName.text = "Customer: ${order.userId.name}"

            val address = order.pickupAddress
            if (address != null) {
                binding.textPickupAddress.text = "Pickup: ${address.street}, ${address.city}"
            } else {
                binding.textPickupAddress.text = "Pickup: Address not available"
            }


            binding.root.setOnClickListener { onOrderClicked(order) }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<AdminOrder>() {
        override fun areItemsTheSame(oldItem: AdminOrder, newItem: AdminOrder): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: AdminOrder, newItem: AdminOrder): Boolean = oldItem == newItem
    }
}