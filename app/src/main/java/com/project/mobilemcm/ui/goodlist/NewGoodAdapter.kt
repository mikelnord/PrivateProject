package com.project.mobilemcm.ui.goodlist

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.ItemGoodListBinding
import com.project.mobilemcm.util.currencyFormat
import com.project.mobilemcm.util.loadImage

class NewGoodAdapter(
    private val action: AdapterAction
) :
    ListAdapter<GoodWithStock, NewGoodAdapter.GoodViewHolder>(NewGoodDiffCallback) {

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
                imgShare.loadImage("https://data.mcmshop.ru/products/${good.id}/main_image?size=thumb")
                amount.text = (good.amount ?: 0.0).toString()
                price.text = currencyFormat((good.price ?: 0.0))
                count.text = good.count.toString()
                rezerv.text = good.reserve.toString()
                if (action.checkGood(good)) {
                    buttonDel.visibility = View.VISIBLE
                } else {
                    buttonDel.visibility = View.INVISIBLE
                }
                when (good.metod) {
                    0, null -> {
                        price2.text = currencyFormat(good.price ?: 0.0)
                        price2.setTextColor(Color.BLACK)
                        summPos.text = currencyFormat((good.price ?: 0.0) * good.count)
                        summPos.setTextColor(Color.BLACK)
                    }

                    1 -> {
                        price2.text = good.priceInd?.let { currencyFormat(it) }
                        price2.setTextColor(Color.GREEN)
                        summPos.text = currencyFormat((good.priceInd ?: 0.0) * good.count)
                        summPos.setTextColor(Color.GREEN)
                    }

                    2 -> {
                        price2.text = good.priceInd?.let { currencyFormat(it) }
                        price2.setTextColor(Color.GREEN)
                        summPos.text = currencyFormat((good.priceInd ?: 0.0) * good.count)
                        summPos.setTextColor(Color.GREEN)
                    }

                    3 -> {
                        price2.text = good.priceInd?.let { currencyFormat(it) }
                        price2.setTextColor(Color.GREEN)
                        summPos.text = currencyFormat((good.priceInd ?: 0.0) * good.count)
                        summPos.setTextColor(Color.GREEN)
                    }

                    4 -> {
                        if (good.count.compareTo(0.0) == 0) {
                            price2.setTextColor(Color.GREEN)
                            good.discont?.let { discont ->
                                price2.text = currencyFormat(
                                    good.price?.minus((good.price.div(100)) * discont) ?: 0.0
                                )
                            }
                            //price.text = currencyFormat(good.price ?: 0.0)
                            summPos.text = String.format("%.2f", 0.0)
                            summPos.setTextColor(Color.GREEN)

                        } else {
                            good.price?.let {
                                good.discont?.let { discont ->
                                    val priceSkid =
                                        good.price.minus((good.price.div(100)) * discont)
                                    price2.text = currencyFormat(priceSkid)
                                    summPos.text = currencyFormat(priceSkid * good.count)
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

object NewGoodDiffCallback : DiffUtil.ItemCallback<GoodWithStock>() {
    override fun areItemsTheSame(oldItem: GoodWithStock, newItem: GoodWithStock): Boolean {
        return ((oldItem.id == newItem.id))
    }

    override fun areContentsTheSame(oldItem: GoodWithStock, newItem: GoodWithStock): Boolean {
        return oldItem == newItem
    }
}