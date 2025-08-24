package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemServiceRecommendationBinding
import com.dhobikart.app.models.LaundryService
import com.dhobikart.app.models.Service

class ServiceRecommendationAdapter(
    private var services: List<LaundryService>,
    private val onBookNowClicked: (LaundryService) -> Unit
) : RecyclerView.Adapter<ServiceRecommendationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemServiceRecommendationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(services[position], onBookNowClicked)
    }

    override fun getItemCount() = services.size

    class ViewHolder(private val binding: ItemServiceRecommendationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(service: LaundryService, onBookNowClicked: (LaundryService) -> Unit) {
            binding.recommendationTitle.text = service.name
            binding.recommendationDescription.text = service.description
            binding.btnBookNow.setOnClickListener { onBookNowClicked(service) }
        }
    }
}
