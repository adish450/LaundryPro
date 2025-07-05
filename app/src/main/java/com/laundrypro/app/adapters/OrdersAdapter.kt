package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemOrderBinding
import com.laundrypro.app.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(private val onOrderClicked: (Order) -> Unit) :
    ListAdapter<Order, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position), onOrderClicked)
    }

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, onOrderClicked: (Order) -> Unit) {
            binding.textOrderId.text = "Order #${order.id.takeLast(6)}"
            binding.chipOrderStatus.text = order.status
            binding.textOrderTotal.text = String.format("Total: ₹%.2f", order.totalAmount)
            binding.textServiceName.text = order.serviceId.name // Access the nested service name

            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(order.createdAt)
                val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                binding.textOrderDate.text = date?.let { formatter.format(it) } ?: order.createdAt
            } catch (e: Exception) {
                binding.textOrderDate.text = order.createdAt
            }

            // Create a detailed summary of the items in the order
            val summary = order.clothes.joinToString(separator = "\n") {
                "${it.quantity} x ${it.clothId}"
            }
            binding.textOrderSummary.text = summary

            binding.root.setOnClickListener { onOrderClicked(order) }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem == newItem
    }
}
