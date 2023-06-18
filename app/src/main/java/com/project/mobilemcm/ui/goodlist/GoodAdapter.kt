package com.project.mobilemcm.ui.goodlist

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.ItemGoodListBinding
import com.project.mobilemcm.util.loadImage

class GoodAdapter(private val list: ArrayList<GoodWithStock>, private val action: AdapterAction) :
    RecyclerView.Adapter<GoodAdapter.GoodViewHolder>() {

    inner class GoodViewHolder(
        private val binding: ItemGoodListBinding,
        private val action: AdapterAction
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
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
                price.text = (good.price ?: 0.0).toString()
                count.text = good.count.toString()
                if (action.checkGood(good)) {
                    buttonDel.visibility = View.VISIBLE
                } else {
                    buttonDel.visibility = View.INVISIBLE
                }
                price2.text = (good.price ?: 0.0).toString()
                price2.setTextColor(-3488560)
                when (good.metod) {
                    0, null -> {
                        price2.text = (good.price ?: 0.0).toString()
                        price2.setTextColor(-3488560)
                    }

                    1 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                    }

                    2 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                    }

                    3 -> {
                        price2.text = good.priceInd.toString()
                        price2.setTextColor(Color.GREEN)
                    }

                    4 -> {
                        if (good.count.compareTo(0.0) == 0) {
                            price2.setTextColor(Color.GREEN)
                            price2.text = (good.price ?: 0.0).toString()
                            price.text = (good.price ?: 0.0).toString()

                        } else {
                            good.price?.let {
                                good.discont?.let {discont->
                                    val summSkid =
                                        (good.count * good.price) - good.count * good.price / 100 * discont
                                    price2.text = String.format("%.2f", (summSkid / good.count))
                                }
                            }
                            price2.setTextColor(Color.GREEN)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GoodViewHolder {
        return GoodViewHolder(
            ItemGoodListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), action
        )
    }

    override fun onBindViewHolder(holder: GoodViewHolder, position: Int) {
        holder.bind(list[position], position)
        //notifyItemChanged(position)
    }

    override fun getItemCount() = list.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newList: List<GoodWithStock>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }

    fun clearList() {
        list.clear()
    }

}

class AdapterAction(
    val addClick: (GoodWithStock, Double) -> Unit,
    val checkGood: (GoodWithStock) -> Boolean,
    val minClick: (GoodWithStock, Double) -> Unit,
    val delClick: (GoodWithStock) -> Unit,
    val longPlusClick: (GoodWithStock, () -> Unit) -> Unit,
    val longMinClick: (GoodWithStock, () -> Unit) -> Unit
)