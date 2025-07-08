package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemOrderServiceBinding
import com.laundrypro.app.models.ServiceOrder

class OrderServiceAdapter(private val services: List<ServiceOrder>) :
    RecyclerView.Adapter<OrderServiceAdapter.ServiceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemOrderServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    class ServiceViewHolder(private val binding: ItemOrderServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(serviceOrder: ServiceOrder) {
            binding.textServiceName.text = serviceOrder.serviceId.name

            val clothesAdapter = OrderClothSummaryAdapter(serviceOrder.clothes)
            binding.recyclerOrderClothes.layoutManager = LinearLayoutManager(binding.root.context)
            binding.recyclerOrderClothes.adapter = clothesAdapter
        }
    }
}