package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.R
import com.dhobikart.app.databinding.ItemServiceBinding
import com.dhobikart.app.models.Service

class ServicesAdapter(private val onServiceClicked: (Service) -> Unit) :
    ListAdapter<Service, ServicesAdapter.ServiceViewHolder>(ServiceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(getItem(position), onServiceClicked)
    }

    class ServiceViewHolder(private val binding: ItemServiceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: Service, onServiceClicked: (Service) -> Unit) {
            binding.serviceName.text = service.name
            binding.serviceDescription.text = service.description

            // Set a placeholder image based on the service name
            val placeholder = when {
                service.name.contains("Express", ignoreCase = true) -> R.drawable.ic_express_delivery_placeholder
                else -> R.drawable.ic_wash_fold_placeholder
            }
            binding.serviceImage.setImageResource(placeholder)

            binding.btnView.setOnClickListener { onServiceClicked(service) }
        }
    }

    class ServiceDiffCallback : DiffUtil.ItemCallback<Service>() {
        override fun areItemsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Service, newItem: Service): Boolean {
            return oldItem == newItem
        }
    }
}
