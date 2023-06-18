package com.project.mobilemcm.ui.categorylist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.DomainCategory
import com.project.mobilemcm.data.local.database.model.DomainCategoryChild
import com.project.mobilemcm.databinding.ItemCategotyListBinding

class CategoryAdapter(
    private val list: ArrayList<DomainCategoryChild>,
    private val childClick: (String) -> Unit,
    private val hideClick: (String) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(
        private val binding: ItemCategotyListBinding, private val childClick: (String) -> Unit,
        private val hideClick: (String) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(parentName:String, childList: List<DomainCategory>) {
            binding.rvSubItem.visibility = View.GONE
            binding.tvParentTitle.setOnClickListener {
                binding.rvSubItem.visibility =
                    if (binding.rvSubItem.isShown) View.GONE else View.VISIBLE
            }
            binding.rvSubItem.adapter = ChildItemAdapter(childList, childClick, hideClick)
            binding.tvParentTitle.text = parentName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            ItemCategotyListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), childClick, hideClick
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(list[position].parentName, list[position].childList)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<DomainCategoryChild>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}