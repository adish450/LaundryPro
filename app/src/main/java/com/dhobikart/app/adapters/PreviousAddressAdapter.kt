package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemPreviousAddressBinding
import com.dhobikart.app.models.Address

class PreviousAddressAdapter(private val onAddressClicked: (Address) -> Unit) :
    ListAdapter<Address, PreviousAddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemPreviousAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position), onAddressClicked)
    }

    class AddressViewHolder(private val binding: ItemPreviousAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, onAddressClicked: (Address) -> Unit) {
            binding.textStreet.text = address.street
            binding.textCityStateZip.text = "${address.city}, ${address.state}, ${address.zip}"
            binding.root.setOnClickListener { onAddressClicked(address) }
        }
    }

    class AddressDiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
}