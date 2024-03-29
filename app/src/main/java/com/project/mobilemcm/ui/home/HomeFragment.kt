package com.project.mobilemcm.ui.home

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationBarView
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentHomeBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel: CategoryViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var savedStateHandle: SavedStateHandle
    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                Log.e("DEBUG", "${it.key} = ${it.value}")
            }
        }

    companion object {
        const val LOGIN_FIRST: String = "LOGIN_FIRST"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
        setupNavigationRail()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val navigator = findNavController()
        val currentBackStackEntry = navigator.currentBackStackEntry!!
        savedStateHandle = currentBackStackEntry.savedStateHandle
        if (homeViewModel.loginRepository.user == null && !savedStateHandle.contains(LoginFragment.LOGIN_SUCCESSFUL))
            savedStateHandle[LoginFragment.LOGIN_SUCCESSFUL] = false

        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry) { success ->
                if (!success) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                    navigator.navigate(R.id.loginFragment, null, navOptions)
                }
            }
        savedStateHandle.getLiveData<Boolean>(LOGIN_FIRST)
            .observe(currentBackStackEntry) {
                it?.let {
                    if (it) {
                        findNavController().navigate(R.id.exchangeFragment)
                    }
                }
            }
    }

    private fun setupUI() {
        homeViewModel.isLoginFirst()
        viewModel.setLaunchPodbor(true)
        homeViewModel.emptyBase.observe(viewLifecycleOwner) {
            savedStateHandle[LOGIN_FIRST] = it
        }
        homeViewModel.dateObmen.observe(viewLifecycleOwner) { obmenDate ->
            obmenDate?.let {
                val dateObmen = SimpleDateFormat(
                    "yyyy-MM-dd'T'HH:mm:ss",
                    Locale.getDefault()
                ).parse(it.dateObmen)
                binding.textDateObmen.text =
                    dateObmen?.let { date ->
                        SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US).format(
                            date
                        )
                    }

            }
        }
        binding.iconButtonExchange.setOnClickListener {
            findNavController().navigate(R.id.exchangeFragment)
        }

        binding.iconButtonList.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestListFragment())
        }

        binding.iconButtonDoc.setOnClickListener {
            viewModel.clearDoc()
            findNavController().navigate(R.id.homeAdapter)
        }

        binding.iconButtonPlan.setOnClickListener {
            //findNavController().navigate(R.id.debitReportFragment)
            //findNavController().navigate(R.id.paymentReportFragment)
        }

        binding.iconButtonDebet.setOnClickListener {
            findNavController().navigate(R.id.debitReportFragment)
        }

        binding.iconButtonPayment.setOnClickListener {
            findNavController().navigate(R.id.paymentReportFragment)
        }

        viewModel.updateAvailable.observe(viewLifecycleOwner) {
            if (it) {
                binding.imageButtonUpdate.visibility = View.VISIBLE
                binding.textViewUpdate.visibility = View.VISIBLE
            } else {
                binding.imageButtonUpdate.visibility = View.INVISIBLE
                binding.textViewUpdate.visibility = View.INVISIBLE
            }
        }

        binding.imageButtonUpdate.setOnClickListener {
            it.isEnabled = false
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_SYNC_SETTINGS,
                    Manifest.permission.REQUEST_INSTALL_PACKAGES
                )
            )
            homeViewModel.data.value?.let { data ->
                homeViewModel.startDownloadingFile(
                    data,
                    success = {
                        homeViewModel.data.value = data.copy().apply {
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
                        homeViewModel.data.value = data.copy().apply {
                            isDownloading = false
                            downloadedUri = it
                        }

                    },
                    running = {
                        homeViewModel.data.value = data.copy().apply {
                            isDownloading = true
                        }
                    },
                    requireContext(),
                    viewLifecycleOwner
                )

            }

        }

        viewModel.getActiveUser().observe(viewLifecycleOwner) { loggedInUser ->
            loggedInUser?.let {
                binding.managerTextView.text = it.displayName
            }
        }
    }

    private fun setupNavigationRail() {
        val navigator = findNavController()
        val navBar = binding.root.findViewById<NavigationBarView>(R.id.navigation_rail)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_list_doc -> {
                    navigator.navigate(HomeFragmentDirections.actionHomeFragmentToRequestListFragment())
                    true
                }

                R.id.menu_add_doc -> {
                    viewModel.clearDoc()
                    navigator.navigate(R.id.homeAdapter)
                    true
                }

                R.id.menu_exchange -> {
                    navigator.navigate(R.id.exchangeFragment)
                    true
                }

                R.id.menu_logout -> {
                    val currentBackStackEntry = findNavController().currentBackStackEntry!!
                    val savedStateHandle = currentBackStackEntry.savedStateHandle
                    savedStateHandle[LoginFragment.LOGIN_SUCCESSFUL] = false
                    homeViewModel.loginRepository.logout()
                    true
                }

                else -> false
            }
        }
    }

}