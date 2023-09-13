package com.project.mobilemcm.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentDebetReportBinding
import com.project.mobilemcm.util.currencyFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DebetReportFragment : Fragment() {

    private var _binding: FragmentDebetReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DebetViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDebetReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        binding.standardSideSheet.visibility = View.INVISIBLE
        val reportsAdapterAction = ReportsAdapterAction { item -> viewModel.onItemClick(item) }
        val adapter = DebetAdapter(arrayListOf(), reportsAdapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.debetList.adapter = adapter
        binding.debetList.addItemDecoration(decoration)
        viewModel.debetList.observe(viewLifecycleOwner) { listDebet ->
            listDebet?.let { adapter.updateData(it) }
        }
        viewModel.showDetail.observe(viewLifecycleOwner) {
            if (it) {
                binding.standardSideSheet.visibility = View.VISIBLE
                viewModel.debetItem?.let { debetItem ->
                    binding.textViewKontr.text = debetItem.client
                    binding.textViewContract.text = debetItem.contract
                    binding.textViewSrok.text = debetItem.delivery
                    binding.textViewDay.text = debetItem.days
                    binding.textViewLimit.text = currencyFormat(debetItem.limit.toDouble())
                    binding.textViewAllSumm.text = currencyFormat(debetItem.debt.toDouble())
                    binding.textViewPDZ.text = currencyFormat(debetItem.overdue_debt.toDouble())
                    binding.textViewPDZ5.text = currencyFormat(debetItem.overdue_debt5.toDouble())
                }
            } else {
                binding.standardSideSheet.visibility = View.INVISIBLE
                viewModel.debetItem = null
            }
        }
        binding.buttonReturn.setOnClickListener {
            viewModel.setShowDetail()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}