package com.laundrypro.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.laundrypro.app.databinding.ItemServiceBinding
import com.laundrypro.app.models.LaundryService

class ServicesAdapter(
    private val onServiceClick: (LaundryService) -> Unit
) : RecyclerView.Adapter<ServicesAdapter.ServiceViewHolder>() {

    private var services = listOf<LaundryService>()

    fun updateServices(newServices: List<LaundryService>) {
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

        fun bind(service: LaundryService) {
            binding.apply {
                textServiceName.text = service.name
                textServiceDescription.text = service.description
                textServiceIcon.text = service.icon
                textServicePrice.text = "Starting at $${String.format("%.2f", service.basePrice)}"

                root.setOnClickListener {
                    onServiceClick(service)
                }
            }
        }
    }
}