package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemOfferBinding
import com.dhobikart.app.models.Offer

class OffersAdapter(private val onOfferClicked: (Offer) -> Unit) :
    ListAdapter<Offer, OffersAdapter.OfferViewHolder>(OfferDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfferViewHolder {
        val binding = ItemOfferBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OfferViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OfferViewHolder, position: Int) {
        holder.bind(getItem(position), onOfferClicked)
    }

    class OfferViewHolder(private val binding: ItemOfferBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(offer: Offer, onOfferClicked: (Offer) -> Unit) {
            binding.offerTitle.text = offer.title
            binding.offerTitle.background = null

            binding.offerDescription.text = offer.description
            binding.offerDescription.background = null

            binding.offerCode.text = offer.code

            binding.textOfferValidUntil.text = "Valid until: ${offer.validUntil}"
            binding.textOfferValidUntil.background = null

            binding.root.setOnClickListener { onOfferClicked(offer) }
        }
    }

    class OfferDiffCallback : DiffUtil.ItemCallback<Offer>() {
        override fun areItemsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Offer, newItem: Offer): Boolean {
            return oldItem == newItem
        }
    }
}
