package com.project.mobilemcm.ui.masterdoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentBasketMasterdocBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.ui.requestDocument.ListDocRequestAdapter
import com.project.mobilemcm.util.showPlusDialog

class BasketFragmentMasterDoc : Fragment() {

    private var _binding: FragmentBasketMasterdocBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBasketMasterdocBinding.inflate(inflater, container, false)
        setupUI()

        return binding.root
    }

    private fun setupUI() {
        binding.floatingActionButton.setOnClickListener{
            val bundle = bundleOf("isMasterDoc" to true)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.podborFragment, true)
                .build()
            findNavController().navigate(R.id.podborFragment, bundle, navOptions)
        }
        setupAdapter()
    }
    private fun setupAdapter() {
        val adapterAction =
            AdapterAction({ goodWithStock, count -> viewModel.addList(goodWithStock, count) },
                { key -> viewModel.inList(key) },
                { good, count -> viewModel.minList(good, count) },
                { good -> viewModel.deleteFromList(good) },
                { good, update -> showAlertDialog(good, update) },
                { good, update -> showAlertDialogMinus(good, update) })
        val adapter = ListDocRequestAdapter(adapterAction)
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.recyclerGoods.addItemDecoration(divider)
        binding.recyclerGoods.adapter = adapter
        viewModel.countList.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.addStringsList.values.toList())
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun showAlertDialog(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            viewModel.addList(goodView, count)
        }
    }

    private fun showAlertDialogMinus(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            good.amount?.let {
                if ((it.minus(count)) < 0) -it else -count
            }?.let {
                viewModel.addList(
                    goodView,
                    it
                )
            }
        }
    }
}