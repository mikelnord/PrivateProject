package com.project.mobilemcm.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentPaymentReportBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PaymentReportFragment : Fragment() {

    private var _binding: FragmentPaymentReportBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PaymentsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPaymentReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
    }

    private fun setupUI() {
        val reportsAdapterAction = ReportsPaymentsAdapterAction { item -> viewModel.onItemClick(item) }
        val adapter = PaymentsAdapter(arrayListOf(), reportsAdapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.debetList.adapter = adapter
        binding.debetList.addItemDecoration(decoration)
        viewModel.paymentsList.observe(viewLifecycleOwner) { listDebet ->
            listDebet?.let { adapter.updateData(it) }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}