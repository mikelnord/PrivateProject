package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentDialogFiltrBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel

class DialogPodborFiltr:DialogFragment(R.layout.fragment_dialog_filtr) {

    private var _binding: FragmentDialogFiltrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding=FragmentDialogFiltrBinding.inflate(inflater,container,false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.searchViewPricegroup.setupWithSearchBar(binding.searchBarPricegroup)
        binding.searchViewVendors.setupWithSearchBar(binding.searchBarVendors)
        val adapterSelectPricegroup = SelectPricegroupAdapter { id -> viewModel.setText(id) }
        val adapterSelectVendors=SelectVendorsAdapter{vendors -> viewModel.setVendor(vendors)}
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.searchRecyclerPricegroup.addItemDecoration(decoration)
        binding.searchRecyclerVendors.addItemDecoration(decoration)
        binding.searchRecyclerPricegroup.adapter = adapterSelectPricegroup
        binding.searchRecyclerVendors.adapter=adapterSelectVendors
        viewModel.findPricegroup.observe(viewLifecycleOwner) { listPricegroup ->
            adapterSelectPricegroup.submitList(listPricegroup)
        }

        viewModel.findVendors.observe(viewLifecycleOwner) { listVendors ->
            adapterSelectVendors.submitList(listVendors)
        }

        binding.searchViewPricegroup.editText.addTextChangedListener { editQuery ->
            viewModel.setQueryPricegroup(editQuery.toString())
        }

        binding.searchViewVendors.editText.addTextChangedListener { editQuery ->
            viewModel.setQueryVendors(editQuery.toString())
        }

        viewModel.selectedPricegroup.observe(viewLifecycleOwner) {
            if(viewModel.isStateFilter.value?.isPrisegroup == true) {
                binding.searchBarPricegroup.text = it.name
                binding.searchViewPricegroup.hide()
            }
        }

        viewModel.selectedVendors.observe(viewLifecycleOwner) {
            if(viewModel.isStateFilter.value?.isVendor == true) {
                binding.searchBarVendors.text = it.name
                binding.searchViewVendors.hide()
            }
        }

        binding.elevatedButton.setOnClickListener{
            dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}