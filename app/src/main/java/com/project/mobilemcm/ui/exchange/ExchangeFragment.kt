package com.project.mobilemcm.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentExchangeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExchangeViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle

    companion object {
        const val LOGIN_FIRST: String = "LOGIN_FIRST"
    }

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        savedStateHandle = findNavController().previousBackStackEntry!!.savedStateHandle

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
            }
        }
    }

    private fun subscribeUi() {
        viewModel.message.observe(viewLifecycleOwner) {
            showError(it)
        }
        viewModel.dateObmen.observe(viewLifecycleOwner) {
            binding.textViewDate.text = it
        }
        viewModel.complateObmen.observe(viewLifecycleOwner) {
            if (it)
                findNavController().navigate(R.id.homeFragment)
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