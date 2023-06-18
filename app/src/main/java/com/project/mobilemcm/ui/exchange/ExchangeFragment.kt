package com.project.mobilemcm.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.project.mobilemcm.databinding.FragmentExchangeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExchangeViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExchangeBinding.inflate(inflater, container, false)
        subscribeUi()
        binding.buttonStartObmen.isEnabled = true
        updateUI()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.buttonStartObmen.setOnClickListener {
            binding.buttonStartObmen.isEnabled = false
            binding.indikator.visibility = View.VISIBLE
            viewModel.getObmen()
            if (!viewModel.isError) {
                viewModel.countGoods.observe(viewLifecycleOwner) {
                    viewModel.insertVendors()
                }
                viewModel.loadFile.observe(viewLifecycleOwner) {
                    binding.indikator.setProgressCompat(it, true)
                }
                viewModel.timeSecGoods.observe(viewLifecycleOwner) {

                }
            }
        }
    }

    private fun subscribeUi() {
        viewModel.message.observe(viewLifecycleOwner) {
            showError(it)
        }
    }

    private fun showError(msg: String) {
        Snackbar.make(binding.vParent, msg, Snackbar.LENGTH_INDEFINITE).setAction("DISMISS") {
        }.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}