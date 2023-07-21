package com.project.mobilemcm.ui.masterdoc

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.databinding.ItemDiscountCompanyBinding
import com.project.mobilemcm.pricing.logic.DiscountCompany

class DiscountCompanyAdapter(private val list: ArrayList<DiscountCompany>) :
    RecyclerView.Adapter<DiscountCompanyAdapter.ItemViewHolder>() {

    class ItemViewHolder(
        private val binding: ItemDiscountCompanyBinding
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(discountCompany: DiscountCompany) {
            binding.textDiscount.text = discountCompany.discount.toString()+" %"
            binding.textPricegroup.text = discountCompany.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ItemDiscountCompanyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(list[position])
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<DiscountCompany>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}