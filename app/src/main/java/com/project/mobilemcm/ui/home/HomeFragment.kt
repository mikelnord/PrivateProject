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
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentHomeBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.login.LoginFragment
import dagger.hilt.android.AndroidEntryPoint


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
        val navController = findNavController()
        val currentBackStackEntry = navController.currentBackStackEntry!!
        savedStateHandle = currentBackStackEntry.savedStateHandle
        if (homeViewModel.loginRepository.user == null && !savedStateHandle.contains(LoginFragment.LOGIN_SUCCESSFUL))
            savedStateHandle[LoginFragment.LOGIN_SUCCESSFUL] = false

        savedStateHandle.getLiveData<Boolean>(LoginFragment.LOGIN_SUCCESSFUL)
            .observe(currentBackStackEntry) { success ->
                if (!success) {
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.loginFragment, true)
                        .build()
                    navController.navigate(R.id.loginFragment, null, navOptions)
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
        homeViewModel.emptyBase.observe(viewLifecycleOwner) {
            savedStateHandle[LOGIN_FIRST] = it
        }
        homeViewModel.dateObmen.observe(viewLifecycleOwner) { obmenDate ->
            obmenDate?.let {
                binding.textDateObmen.text = it.dateObmen
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

        homeViewModel.appMode.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    // binding.dayButton.visibility = View.VISIBLE
                    // binding.nightButton.visibility = View.GONE
                }

                false -> {
                    //  binding.dayButton.visibility = View.GONE
                    //  binding.nightButton.visibility = View.VISIBLE
                }
            }
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

//        binding.dayButton.setOnClickListener {
//            homeViewModel.setMode()
//        }
//
//        binding.nightButton.setOnClickListener {
//            homeViewModel.setMode()
//        }

        binding.imageButtonUpdate.setOnClickListener {
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
                if (it.division_id == "c3a21002-ef22-11e5-a605-f07959941a7c" && viewModel.requestDocument.store_id.isEmpty()) {
                    viewModel.requestDocument.store_id = "ac7265a0-66bb-11df-b7ab-001517890160"
                }
            }
        }

    }

    private fun setupNavigationRail() {
        binding.navigationRail.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_list_doc -> {
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestListFragment())
                    true
                }

                R.id.menu_add_doc -> {
                    viewModel.clearDoc()
                    //findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestDocFragment())
                    true
                }

                R.id.menu_exchange -> {
                    findNavController().navigate(R.id.exchangeFragment)
                    true
                }

                R.id.menu_logout -> {
                    val navController = findNavController()
                    val currentBackStackEntry = navController.currentBackStackEntry!!
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