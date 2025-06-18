package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemOrderBinding
import com.laundrypro.app.models.Order

class OrdersAdapter(private val onOrderClicked: (Order) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    private var orders: List<Order> = emptyList()

    fun updateOrders(newOrders: List<Order>) {
        orders = newOrders
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orders[position], onOrderClicked)
    }

    override fun getItemCount() = orders.size

    class OrderViewHolder(private val binding: ItemOrderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order, onOrderClicked: (Order) -> Unit) {
            binding.orderId.text = "Order #${order.id}"
            binding.orderDate.text = order.createdAt
            binding.orderStatus.text = order.status.displayName
            binding.orderTotal.text = "$${String.format("%.2f", order.totalAmount)}"
            val itemsSummary = order.items.joinToString { "${it.quantity} x ${it.name}" }
            binding.orderItems.text = itemsSummary

            binding.root.setOnClickListener { onOrderClicked(order) }
        }
    }
}
