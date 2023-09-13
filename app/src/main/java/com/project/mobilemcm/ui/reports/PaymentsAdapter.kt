package com.project.mobilemcm.ui.reports


import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.DebetItem
import com.project.mobilemcm.data.local.database.model.PaymentsItem
import com.project.mobilemcm.databinding.PaymentItemBinding
import com.project.mobilemcm.util.currencyFormat
import java.text.SimpleDateFormat
import java.util.Locale

class PaymentsAdapter(
    private val list: ArrayList<PaymentsItem>,
    private val reportsAdapterAction: ReportsPaymentsAdapterAction
) :
    RecyclerView.Adapter<PaymentsAdapter.PaymentsViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PaymentsViewHolder {
        return PaymentsViewHolder(
            PaymentItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), reportsAdapterAction = reportsAdapterAction
        )
    }

    class PaymentsViewHolder(
        private val binding: PaymentItemBinding,
        private val reportsAdapterAction: ReportsPaymentsAdapterAction
    ) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("NotifyDataSetChanged")
        fun bind(paymentsItem: PaymentsItem) {
            with(binding) {
                textClient.text = paymentsItem.client
                textContract.text = paymentsItem.contract
                textSum.text = currencyFormat(paymentsItem.sum.toDouble())
                textNumber.text = paymentsItem.number
                val dateObmen = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                ).parse(paymentsItem.date)
                textDate.text =
                    dateObmen?.let { date ->
                        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(
                            date
                        )
                    }
                card.setOnClickListener { reportsAdapterAction.clickItem.invoke(paymentsItem) }
            }
        }
    }

    override fun onBindViewHolder(holder: PaymentsViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<PaymentsItem>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }
}

class ReportsPaymentsAdapterAction(
    val clickItem: (PaymentsItem) -> Unit
)