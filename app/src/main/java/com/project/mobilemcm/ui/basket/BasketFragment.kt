package com.project.mobilemcm.ui.basket

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentBasketBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.ui.requestDocument.ListDocRequestAdapter
import com.project.mobilemcm.util.showPlusDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BasketFragment : Fragment() {

    private lateinit var binding: FragmentBasketBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBasketBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val adapterAction =
            AdapterAction({ goodWithStock, count -> viewModel.addList(goodWithStock, count) },
                { key -> viewModel.inList(key) },
                { good, count -> viewModel.minList(good, count) },
                { good -> viewModel.deleteFromList(good) },
                { good, update -> showAlertDialog(good, update) },
                { good, update -> showAlertDialogMinus(good, update) })
        val adapter =ListDocRequestAdapter(adapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.basketRecycler.adapter = adapter
        binding.basketRecycler.addItemDecoration(decoration)
        viewModel.countList.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.addStringsList.values.toList())
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