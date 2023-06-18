package com.project.mobilemcm.ui.requestDocument

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.Counterparties
import com.project.mobilemcm.databinding.ItemPricegroupBinding


class CompaniesAdapter(val onClick: (Counterparties) -> Unit) :
    ListAdapter<Counterparties, CompaniesAdapter.RequestViewHolder>(CounterpartiesDiffCallback()) {

    inner class RequestViewHolder(private val binding: ItemPricegroupBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(counterparties: Counterparties) {
            with(binding) {
                tvParentTitle.text = counterparties.name
                card.setOnClickListener {
                    onClick(counterparties)
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

private class CounterpartiesDiffCallback : DiffUtil.ItemCallback<Counterparties>() {
    override fun areItemsTheSame(oldItem: Counterparties, newItem: Counterparties): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Counterparties, newItem: Counterparties): Boolean {
        return oldItem == newItem
    }
}
