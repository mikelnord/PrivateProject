package com.project.mobilemcm.ui.reports

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.DebetItem
import com.project.mobilemcm.databinding.DebetItemBinding
import com.project.mobilemcm.util.currencyFormat

class DebetAdapter(
    private val list: ArrayList<DebetItem>,
    private val reportsAdapterAction: ReportsAdapterAction
) :
    RecyclerView.Adapter<DebetAdapter.DebetViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DebetViewHolder {
        return DebetViewHolder(
            DebetItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), reportsAdapterAction = reportsAdapterAction
        )
    }

    class DebetViewHolder(
        private val binding: DebetItemBinding,
        private val reportsAdapterAction: ReportsAdapterAction
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(debetItem: DebetItem) {
            with(binding) {
                textClient.text = debetItem.client
                textContract.text = debetItem.contract
                textDebt.text = currencyFormat(debetItem.overdue_debt.toDouble())
                textDebt5.text = currencyFormat(debetItem.debt.toDouble())
                card.setOnClickListener { reportsAdapterAction.clickItem.invoke(debetItem)}
            }
        }
    }

    override fun onBindViewHolder(holder: DebetViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<DebetItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }
}

class ReportsAdapterAction(
    val clickItem: (DebetItem) -> Unit
)