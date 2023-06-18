package com.project.mobilemcm.ui.formpodbor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.Pricegroup
import com.project.mobilemcm.databinding.ItemPricegroupBinding

class SelectPricegroupAdapter(val onClick:(String)->Unit) :
    ListAdapter<Pricegroup, SelectPricegroupAdapter.RequestViewHolder>(RequestDiffCallback()){

     inner class RequestViewHolder(private val binding: ItemPricegroupBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(pricegroup: Pricegroup){
            with(binding){
                tvParentTitle.text=pricegroup.name
                card.setOnClickListener {
                    onClick(pricegroup.id)
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
            ))
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private class RequestDiffCallback : DiffUtil.ItemCallback<Pricegroup>() {
    override fun areItemsTheSame(oldItem: Pricegroup, newItem: Pricegroup): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Pricegroup, newItem: Pricegroup): Boolean {
        return oldItem == newItem
    }
}
