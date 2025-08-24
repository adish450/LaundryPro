package com.dhobikart.app.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dhobikart.app.databinding.ItemPaymentMethodBinding
import com.dhobikart.app.models.PaymentMethod

class PaymentMethodsAdapter(private val onClick: (PaymentMethod) -> Unit) :
    ListAdapter<PaymentMethod, PaymentMethodsAdapter.ViewHolder>(PaymentMethodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemPaymentMethodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item, onClick)
    }

    class ViewHolder(private val binding: ItemPaymentMethodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(paymentMethod: PaymentMethod, onClick: (PaymentMethod) -> Unit) {
            binding.paymentMethodName.text = paymentMethod.name
            binding.paymentMethodDetails.text = paymentMethod.details
            binding.paymentMethodIcon.setImageResource(paymentMethod.iconResId)
            itemView.setOnClickListener { onClick(paymentMethod) }
        }
    }
}

class PaymentMethodDiffCallback : DiffUtil.ItemCallback<PaymentMethod>() {
    override fun areItemsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: PaymentMethod, newItem: PaymentMethod): Boolean {
        return oldItem == newItem
    }
}
