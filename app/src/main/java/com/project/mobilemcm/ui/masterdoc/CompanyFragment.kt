package com.project.mobilemcm.ui.masterdoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentCompanyBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.requestDocument.RequestDocFragment
import com.project.mobilemcm.util.currencyFormat


class CompanyFragment : Fragment() {

    private var _binding: FragmentCompanyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCompanyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupUI()

    }

    private fun setupUI() {
        binding.textStore.text = viewModel.requestDocument.store_name
        viewModel.selectedCompanies.observe(viewLifecycleOwner) {
            binding.textCompany.text = it.name
            viewModel.getCompanyInfo(it.id)
        }
        viewModel.companyInfo.observe(viewLifecycleOwner) {
            binding.textDebtValue.text =
                if (it?.debt != null) {
                    currencyFormat(it.debt)
                } else "---"
            binding.textOverdueDebtValue.text = if (it?.overdue_debt != null) {
                currencyFormat(it.overdue_debt)
            } else "---"
            binding.textOverdueDebt5Value.text = if (it?.overdue_debt5 != null) {
                currencyFormat(it.overdue_debt5)
            } else "---"
        }
        viewModel.docSumm.observe(viewLifecycleOwner) { summ ->
            summ?.let {
                binding.textViewSummValue.text = currencyFormat(it.docSumm)
                binding.textViewSkidkaValue.text = currencyFormat(it.docSummSkid)
                binding.next.isEnabled = it.docSumm > 0.0
            }
        }
        viewModel.docCount.observe(viewLifecycleOwner) { count ->
            count?.let {
                binding.textViewProductValue.text = String.format("%.0f", it)
            }
        }
        binding.next.setOnClickListener {
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.basketFragment, true)
                .build()
            findNavController().navigate(R.id.endPage, null, navOptions)

        }
        if (parentFragment is RequestDocFragment) {
            binding.next.visibility = View.GONE
            binding.textViewNumber.text = viewModel.requestDocument.number
        } else {
            binding.textViewNumber.visibility = View.GONE
            binding.toSpisokDoc.visibility = View.GONE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}