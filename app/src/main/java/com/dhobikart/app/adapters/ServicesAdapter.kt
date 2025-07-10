package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemServiceBinding
import com.dhobikart.app.models.Service

class ServicesAdapter(
    private val onServiceClick: (Service) -> Unit
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    private var services = listOf<Service>()

    fun updateServices(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val binding = ItemServiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        holder.bind(services[position])
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(
        private val binding: ItemServiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(service: Service) {
            binding.apply {
                textServiceName.text = service.name
                textServiceDescription.text = service.description
                /*textServiceIcon.text = service.icon
                textServicePrice.text = "Starting at $${String.format("%.2f", service.basePrice)}"*/

                root.setOnClickListener {
                    onServiceClick(service)
                }
            }
        }
    }
}