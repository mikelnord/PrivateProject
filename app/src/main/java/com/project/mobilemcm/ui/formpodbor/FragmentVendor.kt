package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentVendorBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel


class FragmentVendor : Fragment() {

    private var _binding: FragmentVendorBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVendorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        if (viewModel.isStateFilter.value?.isVendor == false) {
            viewModel.setQueryPricegroup("")
            binding.searchViewVendors.show()
        }
    }

    private fun setupUI() {
        binding.searchViewVendors.setupWithSearchBar(binding.searchBarVendors)
        val adapterSelectVendors = SelectVendorsAdapter { vendors -> viewModel.setVendor(vendors) }
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.searchRecyclerVendors.addItemDecoration(decoration)
        binding.searchRecyclerVendors.adapter = adapterSelectVendors
        viewModel.findVendors.observe(viewLifecycleOwner) { listVendors ->
            adapterSelectVendors.submitList(listVendors)
        }
        binding.searchViewVendors.editText.addTextChangedListener { editQuery ->
            viewModel.setQueryVendors(editQuery.toString())
        }
        viewModel.selectedVendors.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isVendor == true) {
                binding.searchBarVendors.text = it?.name
                binding.searchViewVendors.hide()
            } else {
                binding.searchBarVendors.text = ""
                viewModel.setQueryVendors("")
                binding.searchViewVendors.show()
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}