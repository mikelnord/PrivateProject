package com.project.mobilemcm.ui.exchange

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentExchangeBinding
import com.project.mobilemcm.ui.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ExchangeFragment : Fragment() {

    private var _binding: FragmentExchangeBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ExchangeViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
            }
        }

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
            viewModel.getObmen(requireContext())
            //viewModel.applyExchangeWorker(requireContext())
        }
        binding.buttonStartFullObmen.setOnClickListener {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SYNC_SETTINGS,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                )
            )
            viewModel.data.value?.let { data ->
                viewModel.startDownloadingFile(
                    data,
                    success = {
                        viewModel.data.value = data.copy().apply {
                            isDownloading = false
                            downloadedUri = it
                        }
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.setDataAndType(
                            it.toUri(),
                            "application/vnd.android.package-archive"
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or
                            Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                        startActivity(intent)
                    },
                    failed = {
                        viewModel.data.value = data.copy().apply {
                            isDownloading = false
                            downloadedUri = it
                        }

                    },
                    running = {
                        viewModel.data.value = data.copy().apply {
                            isDownloading = true
                        }
                    },
                    requireContext(),
                    viewLifecycleOwner
                )

            }
        }
    }

    private fun subscribeUi() {
        viewModel.message.observe(viewLifecycleOwner) {
            showError(it)
        }
        viewModel.complateObmen.observe(viewLifecycleOwner) {
            if (it) {
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeFragment, true)
                    .build()
                findNavController().navigate(R.id.homeFragment, null, navOptions)
            }
        }
        viewModel.dateObmen.observe(viewLifecycleOwner) {
            binding.textViewDate.text = it
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