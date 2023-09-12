package com.project.mobilemcm.ui.masterdoc

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.project.mobilemcm.util.currencyFormat
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

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.textStore.text = viewModel.requestDocument.store_name
        binding.searchViewAdr.setupWithSearchBar(binding.searchBarAdr)
        val adapterCompaniesAdr = CompaniesAdressAdapter {
            viewModel.setSelectedCompaniesAdr(it)
        }
        binding.searchRecyclerAdr.adapter = adapterCompaniesAdr
        viewModel.getCompanyAdres().observe(viewLifecycleOwner) {
            adapterCompaniesAdr.submitList(it)
            if (it.isEmpty()) {
                viewModel.requestDocument.isPickup = true
                binding.materialCardView4.isEnabled = false
                binding.searchBarAdr.isEnabled = false
                binding.materialCardView.setCardBackgroundColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FB8C00"
                        )
                    )
                )
            }
        }
        binding.materialCardView4.setOnClickListener {
            binding.materialCardView.setCardBackgroundColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#FFFBFF"
                    )
                )
            )
            binding.materialCardView.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFFBFF")))
            binding.materialCardView4.setCardBackgroundColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#FB8C00"
                    )
                )
            )
            binding.materialCardView4.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#1E1E1E")))
            viewModel.requestDocument.isPickup = false

        }
        binding.materialCardView.setOnClickListener {
            binding.materialCardView4.setCardBackgroundColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#FFFBFF"
                    )
                )
            )
            binding.materialCardView4.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFFBFF")))
            binding.materialCardView.setCardBackgroundColor(
                ColorStateList.valueOf(
                    Color.parseColor(
                        "#FB8C00"
                    )
                )
            )
            binding.materialCardView.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#1E1E1E")))
            viewModel.requestDocument.isPickup = true
        }

        binding.searchViewAdr.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.setQueryCompaniesAdr(binding.searchViewAdr.text.toString())
            false
        }
        viewModel.selectedCompaniesAdr.observe(viewLifecycleOwner) {
            if (!it.address.isNullOrEmpty()) {
                binding.searchBarAdr.text = it.address
                binding.searchViewAdr.hide()
                viewModel.requestDocument.counterpartiesStores_id = it.id
                binding.materialCardView.setCardBackgroundColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FFFBFF"
                        )
                    )
                )
                binding.materialCardView.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#FFFBFF")))
                binding.materialCardView4.setCardBackgroundColor(
                    ColorStateList.valueOf(
                        Color.parseColor(
                            "#FB8C00"
                        )
                    )
                )
                binding.materialCardView4.setStrokeColor(ColorStateList.valueOf(Color.parseColor("#1E1E1E")))
                viewModel.requestDocument.isPickup = false
            }
        }
        binding.buttonSave.setOnClickListener {
            if (viewModel.requestDocument.counterpartiesStores_id.isNotEmpty() || viewModel.requestDocument.isPickup) {
                viewModel.requestDocument.comment =
                    binding.textCardComm.editText?.text.toString().trim()
                if (!viewModel.saveDoc()) showAlert(
                    requireContext(),
                    "Сумма документа превышает разрешенную сумму по договору",
                    "Измените договор или уменьшите сумму"
                )
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
                binding.textViewSummValue.text = currencyFormat(it.docSumm)
            }
        }
        viewModel.docCount.observe(viewLifecycleOwner) { count ->
            count?.let {
                binding.textViewProductValue.text = String.format("%.0f", it)
            }

        }
        viewModel.selectedCompanies.observe(viewLifecycleOwner) {
            binding.searchBarAdr.clearText()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}