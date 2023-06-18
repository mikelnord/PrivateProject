package com.project.mobilemcm.ui.requestDocument

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.CounterpartiesStores
import com.project.mobilemcm.databinding.ItemPricegroupBinding


class CompaniesAdressAdapter(val onClick: (CounterpartiesStores) -> Unit) :
    ListAdapter<CounterpartiesStores, CompaniesAdressAdapter.RequestViewHolder>(CounterpartiesStoresDiffCallback()) {

    inner class RequestViewHolder(private val binding: ItemPricegroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(counterpartiesStores: CounterpartiesStores) {
            with(binding) {
                tvParentTitle.text = counterpartiesStores.address
                card.setOnClickListener {
                    onClick(counterpartiesStores)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(
            ItemPricegroupBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

private class CounterpartiesStoresDiffCallback : DiffUtil.ItemCallback<CounterpartiesStores>() {
    override fun areItemsTheSame(oldItem: CounterpartiesStores, newItem: CounterpartiesStores): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: CounterpartiesStores, newItem: CounterpartiesStores): Boolean {
        return oldItem == newItem
    }
}