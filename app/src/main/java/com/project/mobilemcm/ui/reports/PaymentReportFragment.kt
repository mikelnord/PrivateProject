package com.project.mobilemcm.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentPaymentReportBinding
import com.project.mobilemcm.util.currencyFormat
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

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
        val reportsAdapterAction =
            ReportsPaymentsAdapterAction { item -> viewModel.onItemClick(item) }
        val adapter = PaymentsAdapter(arrayListOf(), reportsAdapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.paymentsList.adapter = adapter
        binding.paymentsList.addItemDecoration(decoration)
        viewModel.paymentsList.observe(viewLifecycleOwner) { listDebet ->
            listDebet?.let { adapter.updateData(it) }
        }
        viewModel.showDetail.observe(viewLifecycleOwner) {
            if (it) {
                binding.standardSideSheet.visibility = View.VISIBLE
                viewModel.paymentsItem?.let { paymentsItem ->
                    binding.textViewKontr.text = paymentsItem.client
                    binding.textViewContract.text = paymentsItem.contract
                    val dateObmen = SimpleDateFormat(
                        "yyyy-MM-dd'T'HH:mm:ss",
                        Locale.getDefault()
                    ).parse(paymentsItem.date)
                    binding.textViewDate.text =
                        dateObmen?.let { date ->
                            SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(
                                date
                            )
                        }
                    binding.textViewNumber.text = paymentsItem.number
                    binding.textViewSumm.text = currencyFormat(paymentsItem.sum.toDouble())
                    binding.textViewPurpose.text = paymentsItem.purpose
                }
            } else {
                binding.standardSideSheet.visibility = View.INVISIBLE
                viewModel.paymentsItem = null
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