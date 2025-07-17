package com.dhobikart.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemManageAddressBinding
import com.dhobikart.app.models.Address

class ManageAddressAdapter(
    private val onEditClicked: (Address) -> Unit,
    private val onDeleteClicked: (Address) -> Unit
) : ListAdapter<Address, ManageAddressAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemManageAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onEditClicked, onDeleteClicked)
    }

    class ViewHolder(private val binding: ItemManageAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, onEdit: (Address) -> Unit, onDelete: (Address) -> Unit) {
            binding.textStreet.text = address.street
            binding.textCityStateZip.text = "${address.city}, ${address.state}, ${address.zip}"
            binding.btnEdit.setOnClickListener { onEdit(address) }
            binding.btnDelete.setOnClickListener { onDelete(address) }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
        override fun areContentsTheSame(oldItem: Address, newItem: Address) = oldItem == newItem
    }
}