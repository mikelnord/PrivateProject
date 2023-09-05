package com.project.mobilemcm.ui.exchange

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.snackbar.Snackbar
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentExchangeBinding
import com.project.mobilemcm.ui.home.HomeFragment
import com.project.mobilemcm.workers.ExchangeWorker.Companion.Progress
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
        updateUI()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        findNavController().previousBackStackEntry?.let {
            if (it.savedStateHandle.get<Boolean>(HomeFragment.LOGIN_FIRST) == true)
                binding.textViewDate.text =
                    "Для работы необходимо выполнить первоначальное заполнение базы"
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        binding.buttonStartObmen.isEnabled = true
        binding.buttonStartFullObmen.isEnabled = true
        binding.buttonStartObmen.setOnClickListener {
            binding.buttonStartObmen.isEnabled = false
            binding.buttonStartFullObmen.isEnabled = false
            binding.indikator.visibility = View.VISIBLE
            viewModel.applyExchangeWorker(requireContext())
            viewModel.applyWorker(requireContext())
            WorkManager.getInstance(requireContext())
                .getWorkInfoByIdLiveData(viewModel.idWork)
                .observe(viewLifecycleOwner) { workInfo: WorkInfo? ->
                    if (workInfo != null) {
                        val progress = workInfo.progress
                        val value = progress.getInt(Progress, 0)
                        when (value) {
                            10 -> binding.textViewDate.text = "Загрузка данных с сервера"
                            50 -> {
                                binding.indikator.setProgressCompat(value, true)
                                binding.textViewDate.text = "Обновление базы"
                            }

                            100 -> {
                                binding.indikator.setProgressCompat(value, true)
                                binding.textViewDate.text = "Выполненно"
                            }
                        }

                        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                            val navOptions = NavOptions.Builder()
                                .setPopUpTo(R.id.homeFragment, true)
                                .build()
                            findNavController().navigate(R.id.homeFragment, null, navOptions)
                        }
                    }
                }

        }
        binding.buttonStartFullObmen.setOnClickListener {
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