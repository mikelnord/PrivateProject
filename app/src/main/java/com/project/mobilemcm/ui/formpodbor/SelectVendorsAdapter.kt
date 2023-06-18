package com.project.mobilemcm.ui.formpodbor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.Vendors
import com.project.mobilemcm.databinding.ItemPricegroupBinding

class SelectVendorsAdapter(val onClick:(Vendors)->Unit) :
    ListAdapter<Vendors, SelectVendorsAdapter.RequestViewHolder>(VendorsDiffCallback()){

    inner class RequestViewHolder(private val binding: ItemPricegroupBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(vendors: Vendors){
            with(binding){
                tvParentTitle.text=vendors.name
                card.setOnClickListener {
                    onClick(vendors)
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

private class VendorsDiffCallback : DiffUtil.ItemCallback<Vendors>() {
    override fun areItemsTheSame(oldItem: Vendors, newItem: Vendors): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Vendors, newItem: Vendors): Boolean {
        return oldItem == newItem
    }
}
