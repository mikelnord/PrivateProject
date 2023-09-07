package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentPodborFiltrBinding
import com.project.mobilemcm.ui.categorylist.CategoryListFragment
import com.project.mobilemcm.ui.categorylist.CategoryViewModel


class PodborFiltrFragment : Fragment() {

    private var _binding: FragmentPodborFiltrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodborFiltrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        binding.ButtonCategori.setOnClickListener {
            childFragmentManager.commit {
                replace(R.id.fragmentContainer, CategoryListFragment())
                setReorderingAllowed(true)
            }
        }
        binding.ButtonVendor.setOnClickListener {
            childFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentVendor())
                setReorderingAllowed(true)
            }
        }
        binding.ButtonPriceGroup.setOnClickListener {
            childFragmentManager.commit {
                replace(R.id.fragmentContainer, FragmentPricegroup())
                setReorderingAllowed(true)
            }
        }

        viewModel.currentCategoryName.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.chipCategory.text = it
                binding.chipCategory.isChecked = true
                binding.chipCategory.visibility = View.VISIBLE
            } else {
                binding.chipCategory.visibility = View.INVISIBLE
            }
        }
        viewModel.selectedPricegroup.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isPrisegroup == true) {
                binding.chipPricegroup.text = it?.name
                binding.chipPricegroup.isChecked = true
                binding.chipPricegroup.visibility = View.VISIBLE
            } else {
                binding.chipPricegroup.visibility = View.GONE
            }
        }
        binding.chipPricegroup.setOnClickListener {
            it.visibility = View.GONE
            viewModel.setStatePricegroup()
            viewModel.clearSelectedPriceGroup()
        }
        viewModel.selectedVendors.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isVendor == true) {
                binding.chipVendor.text = it?.name
                binding.chipVendor.isChecked = true
                binding.chipVendor.visibility = View.VISIBLE
            } else {
                binding.chipVendor.visibility = View.GONE
            }
        }
        binding.chipVendor.setOnClickListener {
            it.visibility = View.GONE
            viewModel.setStateVendor()
            viewModel.clearSelectedVendors()
        }

        viewModel.clearAll.observe(viewLifecycleOwner) {
            if (it) {
                binding.chipClearAll.visibility = View.VISIBLE
                binding.chipClearAll.isChecked = true
            } else binding.chipClearAll.visibility = View.INVISIBLE
        }
        binding.chipClearAll.setOnClickListener {
            viewModel.clearStateFilter()
            viewModel.setClearAll(false)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}