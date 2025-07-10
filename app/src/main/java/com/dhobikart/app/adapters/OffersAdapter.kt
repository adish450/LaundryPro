package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemOfferBinding
import com.dhobikart.app.models.Offer

class OffersAdapter(private val onOfferClicked: (Offer) -> Unit) : RecyclerView.Adapter<OffersAdapter.OfferViewHolder>() {

    private var offers: List<Offer> = emptyList()

    fun updateOffers(newOffers: List<Offer>) {
        offers = newOffers
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(offers[position], onOfferClicked)
    }

    override fun getItemCount() = offers.size

    class OfferViewHolder(private val binding: ItemOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer, onOfferClicked: (Offer) -> Unit) {
            binding.offerTitle.text = offer.title
            binding.offerDescription.text = offer.description
            binding.offerCode.text = offer.code
            binding.textOfferValidUntil.text = "Valid until: ${offer.validUntil}" // This line is now correct

            binding.root.setOnClickListener { onOfferClicked(offer) }
        }
    }
}