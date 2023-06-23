package com.project.mobilemcm.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

    companion object {
        const val LOGIN_FIRST: String = "LOGIN_FIRST"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupUI()
        setupNavigationRail()
        homeViewModel.isLoginFirst()
        savedStateHandle[LOGIN_FIRST] = homeViewModel.emptyBase.value
        return binding.root
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

        binding.iconButtonExchange.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToExchangeFragment())
        }

        binding.iconButtonList.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToRequestListFragment())
        }

        binding.iconButtonDoc.setOnClickListener {
            viewModel.clearDoc()
            val navOptions = NavOptions.Builder()
                //.setPopUpTo(R.id.homeAdapter, false)
                // .setLaunchSingleTop(true)
                .build()
            findNavController().navigate(R.id.homeAdapter)
        }

        homeViewModel.appMode.observe(viewLifecycleOwner) {
            when (it) {
                true -> {
                    binding.dayButton.visibility = View.VISIBLE
                    binding.nightButton.visibility = View.GONE
                }

                false -> {
                    binding.dayButton.visibility = View.GONE
                    binding.nightButton.visibility = View.VISIBLE
                }
            }
        }

        binding.dayButton.setOnClickListener {
            homeViewModel.setMode()
        }

        binding.nightButton.setOnClickListener {
            homeViewModel.setMode()
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
                    findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToExchangeFragment())
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