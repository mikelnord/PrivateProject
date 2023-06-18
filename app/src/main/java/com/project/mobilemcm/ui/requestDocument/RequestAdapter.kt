package com.project.mobilemcm.ui.requestDocument

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.RequestDocumentItem
import com.project.mobilemcm.databinding.ItemRequestDocBinding
import java.text.SimpleDateFormat
import java.util.Locale

class RequestAdapter(val onClick: (Long) -> Unit) :
    ListAdapter<RequestDocumentItem, RequestAdapter.RequestViewHolder>(RequestDiffCallback()){

   inner class RequestViewHolder(private val binding: ItemRequestDocBinding):RecyclerView.ViewHolder(binding.root) {

        fun bind(requestDocumentItem: RequestDocumentItem){
            val dateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            with(binding){
                date.text=dateFormat.format(requestDocumentItem.docDate.time)
                number.text=requestDocumentItem.document_id.toString()
                store.text=requestDocumentItem.nameStore
                counterparties.text=requestDocumentItem.nameCounterparties
                card.setOnClickListener {
                    onClick(requestDocumentItem.document_id)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        return RequestViewHolder(
            ItemRequestDocBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ))
    }

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private class RequestDiffCallback : DiffUtil.ItemCallback<RequestDocumentItem>() {
    override fun areItemsTheSame(oldItem: RequestDocumentItem, newItem: RequestDocumentItem): Boolean {
        return oldItem.document_id == newItem.document_id
    }

    override fun areContentsTheSame(oldItem: RequestDocumentItem, newItem: RequestDocumentItem): Boolean {
        return oldItem == newItem
    }
}
