package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemSelectableAddressBinding
import com.dhobikart.app.models.Address

class SelectableAddressAdapter(
    private val onAddressSelected: (Address) -> Unit
) : ListAdapter<Address, SelectableAddressAdapter.ViewHolder>(DiffCallback()) {

    private var selectedPosition = 0

    fun getSelectedAddress(): Address? {
        return if (selectedPosition in 0 until itemCount) getItem(selectedPosition) else null
    }

    fun setSelectedAddress(address: Address) {
        val index = currentList.indexOf(address)
        if (index != -1) {
            notifyItemChanged(selectedPosition)
            selectedPosition = index
            notifyItemChanged(selectedPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemSelectableAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), position == selectedPosition) {
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onAddressSelected(getItem(selectedPosition))
        }
    }

    class ViewHolder(private val binding: ItemSelectableAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, isSelected: Boolean, onClick: () -> Unit) {
            binding.textStreet.text = address.street
            binding.textCityStateZip.text = "${address.city}, ${address.state}, ${address.zip}"
            binding.radioButton.isChecked = isSelected
            binding.root.setOnClickListener { onClick() }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
    }
}