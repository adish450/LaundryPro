package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemOrderBinding
import com.dhobikart.app.models.Order
import java.text.SimpleDateFormat
import java.util.*

class OrdersAdapter(private val onOrderClicked: (Order) -> Unit) :
    ListAdapter<Order, OrdersAdapter.OrderViewHolder>(OrderDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, onOrderClicked)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onOrderClicked: (Order) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.textOrderId.text = "Order #${order.id.takeLast(6)}"
            binding.chipOrderStatus.text = order.status
            binding.textOrderTotal.text = String.format("Total: ₹%.2f", order.totalAmount)

            try {
                val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
                parser.timeZone = TimeZone.getTimeZone("UTC")
                val date = parser.parse(order.createdAt)
                val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
                binding.textOrderDate.text = date?.let { formatter.format(it) } ?: order.createdAt
            } catch (e: Exception) {
                binding.textOrderDate.text = order.createdAt
            }

            // Set up the nested RecyclerView for the services list.
            val serviceAdapter = OrderServiceAdapter(order.services)
            binding.recyclerOrderServices.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerOrderServices.adapter = serviceAdapter


            binding.root.setOnClickListener { /*onOrderClicked(order)*/ }
        }
    }

    class OrderDiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean = oldItem == newItem
    }
}