package com.project.mobilemcm.ui.requestDocument

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.ItemGoodListBinding
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.util.loadImage

class ListDocRequestAdapter(
    private val action: AdapterAction
) :
    ListAdapter<GoodWithStock, ListDocRequestAdapter.GoodViewHolder>(GoodDiffCallback) {

    inner class GoodViewHolder(
        private val binding: ItemGoodListBinding,
        private val action: AdapterAction
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(good: GoodWithStock, position: Int) {
            with(binding) {
                buttonPlus.setOnClickListener {
                    action.addClick(good, 1.0)
                    notifyItemChanged(position)
                }
                buttonPlus.setOnLongClickListener {
                    action.longPlusClick(good) { notifyItemChanged(position) }
                    true
                }
                buttonMinus.setOnLongClickListener {
                    action.longMinClick(good) { notifyItemChanged(position) }
                    true
                }
                buttonMinus.setOnClickListener {
                    action.minClick(good, 1.0)
                    notifyItemChanged(position)
                }
                buttonDel.setOnClickListener {
                    action.delClick(good)
                    notifyItemChanged(position)
                }
                tvGoodTitle.text = good.name
                tvGoodCode.text = good.vendorCode
                val countValue = good.count
                imgShare.loadImage("https://data.mcmshop.ru/products/${good.id}/main_image?size=thumb")
                price.text = (good.price ?: 0.0).toString()
                count.text = countValue.toString()
                textSklad.visibility = View.INVISIBLE
                textRezerv.visibility = View.INVISIBLE
                if (action.checkGood(good)) {
                    buttonMinus.visibility = View.VISIBLE
                    buttonDel.visibility = View.VISIBLE
                } else {
                    buttonMinus.visibility = View.INVISIBLE
                    buttonDel.visibility = View.INVISIBLE
                }
                when (good.metod) {
                    0, null -> {
                        price2.text = (good.price ?: 0.0).toString()
                        price2.setTextColor(Color.BLACK)
                        summPos.text = String.format("%.2f", ((good.price ?: 0.0) * good.count))
                        summPos.setTextColor(Color.BLACK)
                    }

                    1 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                        summPos.text = String.format("%.2f", ((good.priceInd ?: 0.0) * good.count))
                        summPos.setTextColor(Color.GREEN)
                    }

                    2 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                        summPos.text = String.format("%.2f", ((good.priceInd ?: 0.0) * good.count))
                        summPos.setTextColor(Color.GREEN)
                    }

                    3 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                        summPos.text = String.format("%.2f", ((good.priceInd ?: 0.0) * good.count))
                        summPos.setTextColor(Color.GREEN)
                    }

                    4 -> {
                        if (good.count.compareTo(0.0) == 0) {
                            price2.setTextColor(Color.GREEN)
                            price2.text = (good.price ?: 0.0).toString()
                            price.text = (good.price ?: 0.0).toString()
                            summPos.text = String.format("%.2f", 0.0)
                            summPos.setTextColor(Color.GREEN)

                        } else {
                            good.price?.let {
                                good.discont?.let { discont ->
                                    val summSkid =
                                        (good.count * good.price) - good.count * good.price / 100 * discont
                                    price2.text = String.format("%.2f", (summSkid / good.count))
                                    summPos.text =
                                        String.format("%.2f", (summSkid / good.count) * good.count)
                                }
                            }
                            price2.setTextColor(Color.GREEN)
                            summPos.setTextColor(Color.GREEN)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoodViewHolder {
        return GoodViewHolder(
            ItemGoodListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), action
        )
    }


    override fun onBindViewHolder(holder: GoodViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }

}

object GoodDiffCallback : DiffUtil.ItemCallback<GoodWithStock>() {
    override fun areItemsTheSame(oldItem: GoodWithStock, newItem: GoodWithStock): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: GoodWithStock, newItem: GoodWithStock): Boolean {
        return oldItem == newItem
    }
}

