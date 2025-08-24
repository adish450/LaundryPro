package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemOfferBinding
import com.dhobikart.app.models.Offer

class OffersAdapter(
    private var offers: List<Offer>,
    private val onShopNowClicked: (Offer) -> Unit
) : RecyclerView.Adapter<OffersAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(offers[position], onShopNowClicked)
    }

    override fun getItemCount() = offers.size

    class ViewHolder(private val binding: ItemOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer, onShopNowClicked: (Offer) -> Unit) {
            binding.offerTitle.text = offer.title
            binding.offerDescription.text = offer.description
            binding.btnShopNow.setOnClickListener { onShopNowClicked(offer) }
        }
    }
}