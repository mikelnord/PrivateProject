package com.project.mobilemcm.ui.categorylist

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.DomainCategory
import com.project.mobilemcm.databinding.SubListItemBinding

class ChildItemAdapter(private val subList: List<DomainCategory>, private val childClick: (String) -> Unit,
                       private val hideClick: (String)->Unit) :
    RecyclerView.Adapter<ChildItemAdapter.ChildViewHolder>() {

    class ChildViewHolder(
        private val binding: SubListItemBinding,
        private val childClick: (String) -> Unit,
        private val hideClick: (String)->Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(category: DomainCategory) {
            binding.tvSubItemTitle.setOnClickListener {
                hideClick.invoke(category.name)
                childClick(category.id)
            }
            binding.tvSubItemTitle.text = category.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildViewHolder {
        return ChildViewHolder(
            SubListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), childClick, hideClick
        )
    }

    override fun getItemCount() = subList.size

    override fun onBindViewHolder(holder: ChildViewHolder, position: Int) {
        holder.bind(subList[position])
    }


}