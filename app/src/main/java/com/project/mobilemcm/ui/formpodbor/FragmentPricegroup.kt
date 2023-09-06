package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentPricegroupBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel


class FragmentPricegroup : Fragment() {

    private var _binding: FragmentPricegroupBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPricegroupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        if (viewModel.isStateFilter.value?.isPrisegroup == false) {
            viewModel.setQueryPricegroup("")
            binding.searchViewPricegroup.show()
        }
    }

    private fun setupUI() {
        binding.searchViewPricegroup.setupWithSearchBar(binding.searchBarPricegroup)
        val adapterSelectPricegroup = SelectPricegroupAdapter { id -> viewModel.setText(id) }
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.searchRecyclerPricegroup.addItemDecoration(decoration)
        binding.searchRecyclerPricegroup.adapter = adapterSelectPricegroup
        viewModel.findPricegroup.observe(viewLifecycleOwner) { listPricegroup ->
            adapterSelectPricegroup.submitList(listPricegroup)
        }

        binding.searchViewPricegroup.editText.addTextChangedListener { editQuery ->
            viewModel.setQueryPricegroup(editQuery.toString())
        }

        viewModel.selectedPricegroup.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isPrisegroup == true) {
                binding.searchBarPricegroup.text = it?.name
                binding.searchViewPricegroup.hide()
            } else {
                binding.searchBarPricegroup.text = ""
                viewModel.setQueryPricegroup("")
                binding.searchViewPricegroup.show()
            }
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}