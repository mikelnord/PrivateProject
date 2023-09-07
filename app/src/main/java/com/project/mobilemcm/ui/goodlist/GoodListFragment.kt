package com.project.mobilemcm.ui.goodlist

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentGoodListBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.util.showPlusDialog


class GoodListFragment : Fragment() {

    private lateinit var binding: FragmentGoodListBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGoodListBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupUI() {
        val adapterAction =
            AdapterAction({ goodWithStock, count -> viewModel.addList(goodWithStock, count) },
                { key -> viewModel.inList(key) },
                { good, count -> viewModel.minList(good, count) },
                { good -> viewModel.deleteFromList(good) },
                { good, update -> showAlertDialog(good, update) },
                { good, update -> showAlertDialogMinus(good, update) })
        val adapter = NewGoodAdapter(adapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerGoods.adapter = adapter
        binding.recyclerGoods.addItemDecoration(decoration)
        viewModel.listGood.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            if(it.isEmpty()) binding.notFind.visibility  =View.VISIBLE
            else binding.notFind.visibility  =View.INVISIBLE
        }
        viewModel.countList.observe(viewLifecycleOwner) {
            viewModel.listGood.value?.let {
                adapter.submitList(viewModel.sync(it))
            }
        }
    }

    private fun showAlertDialog(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            good.amount?.let {
                if ((it.minus(count)) < 0) it else count
            }?.let {
                viewModel.addList(
                    goodView,
                    it
                )
            }
        }
    }

    private fun showAlertDialogMinus(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            good.amount?.let {
                if ((it.minus(count)) < 0) -it else -count//код проверки перенести в модель
            }?.let {
                viewModel.addList(
                    goodView,
                    it
                )
            }
        }
    }
}