package com.project.mobilemcm.ui.categorylist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.DomainCategory
import com.project.mobilemcm.databinding.SubListItemBinding

class ChildItemAdapter(
    private val subList: List<DomainCategory>, private val childClick: (String, String) -> Unit,
    private val currentCategory: (MutableLiveData<String?>)
) :
    RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder>() {

    class ChildViewHolder(
        private val binding: SubListItemBinding,
        private val childClick: (String, String) -> Unit,
        private val currentCategory: (MutableLiveData<String?>)
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: DomainCategory) {
            binding.tvSubItemTitle.setOnClickListener {
                childClick(category.id, category.name)
            }
            binding.tvSubItemTitle.text = category.name
            val colorDefault = binding.card.cardBackgroundColor
            currentCategory.observeForever {
                if (it == category.id) {
                    binding.card.setCardBackgroundColor(Color.YELLOW)
                } else {
                    binding.card.setCardBackgroundColor(colorDefault)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(
            SubListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), childClick, currentCategory
        )
    }

    override fun getItemCount() = subList.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(subList[position])
    }

}