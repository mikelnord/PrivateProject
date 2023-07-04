package com.project.mobilemcm.ui.masterdoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentEndPageBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.requestDocument.CompaniesAdressAdapter
import com.project.mobilemcm.util.showAlert

class EndPage : Fragment() {

    private var _binding: FragmentEndPageBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEndPageBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        binding.searchViewAdr.setupWithSearchBar(binding.searchBarAdr)
        val adapterCompaniesAdr = CompaniesAdressAdapter {
            viewModel.setSelectedCompaniesAdr(it)
        }
        binding.searchRecyclerAdr.adapter = adapterCompaniesAdr
        viewModel.getCompanyAdres().observe(viewLifecycleOwner) {
            adapterCompaniesAdr.submitList(it)
        }
        binding.searchViewAdr.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.setQueryCompaniesAdr(binding.searchViewAdr.text.toString())
            false
        }
        viewModel.selectedCompaniesAdr.observe(viewLifecycleOwner) {
            binding.searchBarAdr.text = it.address
            binding.searchViewAdr.hide()
            viewModel.requestDocument.counterpartiesStores_id = it.id
        }
        binding.buttonSave.setOnClickListener {
            if (viewModel.requestDocument.counterpartiesStores_id.isNotEmpty() || viewModel.requestDocument.isPickup) {
                viewModel.requestDocument.comment = binding.textCardComm.text.toString().trim()
                if (!viewModel.saveDoc()) showAlert(requireContext())
                else {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.homeFragment, false)
                        .build()
                    findNavController().navigate(R.id.requestListFragment, null, navOptions)
                }
            } else {
                Toast.makeText(requireContext(), "Место доставки!", Toast.LENGTH_LONG).show()
            }
        }
        viewModel.docSumm.observe(viewLifecycleOwner) { summ ->
            summ?.let {
                binding.textCardSumm.text = String.format("На сумму %.2f", it)
            }
        }
        viewModel.docCount.observe(viewLifecycleOwner) { count ->
            count?.let {
                binding.textCardCount.text = String.format("Количество товаров %.2f", it)
            }

        }
        binding.switchAdress.setOnCheckedChangeListener { buttonView, isChecked ->
            when (isChecked) {
                true -> {
                    binding.searchBarAdr.isEnabled = false
                    binding.searchBarAdr.text = "Самовывоз"
                    viewModel.requestDocument.isPickup = true
                }

                false -> {
                    binding.searchBarAdr.isEnabled = true
                    binding.searchBarAdr.clearText()
                    viewModel.requestDocument.isPickup = false
                }
            }
        }
        viewModel.selectedCompanies.observe(viewLifecycleOwner){
            binding.searchBarAdr.clearText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}