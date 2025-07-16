package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemAddressSelectableBinding
import com.dhobikart.app.models.Address

class AddressAdapter : ListAdapter<Address, AddressAdapter.AddressViewHolder>(AddressDiffCallback()) {

    private var selectedPosition = -1

    fun getSelectedAddress(): Address? {
        return if (selectedPosition != -1) getItem(selectedPosition) else null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressSelectableBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        holder.bind(getItem(position), position, selectedPosition) { newPosition ->
            val oldPosition = selectedPosition
            selectedPosition = newPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(newPosition)
        }
    }

    class AddressViewHolder(private val binding: ItemAddressSelectableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, position: Int, selectedPosition: Int, onAddressClicked: (Int) -> Unit) {
            binding.textStreet.text = address.street
            binding.textCityStateZip.text = "${address.city}, ${address.state}, ${address.zip}"
            binding.radioButton.isChecked = position == selectedPosition

            binding.root.setOnClickListener {
                onAddressClicked(position)
            }
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