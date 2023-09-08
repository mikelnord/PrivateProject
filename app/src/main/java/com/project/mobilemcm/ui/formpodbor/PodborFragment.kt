package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.navigation.NavigationBarView
import com.google.android.material.search.SearchView
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentPodborBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.ui.goodlist.NewGoodAdapter
import com.project.mobilemcm.util.showPlusDialog


class PodborFragment : Fragment() {

    private var _binding: FragmentPodborBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()
    private val args: PodborFragmentArgs by navArgs()
    private var navBar: NavigationBarView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodborBinding.inflate(inflater, container, false)
        navBar = binding.root.findViewById(R.id.navigation_rail)
        setupUI()
        setupFind()
        setupNavigationRail()
        return binding.root
    }

    private fun setupFind() {
        if (viewModel.firstLaunchPodbor) {
            viewModel.setRemainder(true)
            findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToPodborFiltrFragment())
            viewModel.setFirstLaunchPodbor()
        }
        binding.searchView.setupWithSearchBar(binding.searchBar)
        viewModel.isStateFilter.observe(viewLifecycleOwner) {
            binding.chipAmount.isChecked = it.isRemainder
        }
        binding.chipAmount.setOnClickListener {
            viewModel.setRemainder(!(viewModel.isStateFilter.value?.isRemainder ?: false))
        }
        binding.chipPricegroup.setOnClickListener {
            it.visibility = View.GONE
            viewModel.setStatePricegroup()
            viewModel.clearSelectedPriceGroup()
        }
        binding.chipVendor.setOnClickListener {
            it.visibility = View.GONE
            viewModel.setStateVendor()
            viewModel.clearSelectedVendors()
        }

        viewModel.selectedPricegroup.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isPrisegroup == true) {
                binding.chipPricegroup.text = it?.name
                binding.chipPricegroup.isChecked = true
                binding.chipPricegroup.visibility = View.VISIBLE
            }
        }

        viewModel.selectedVendors.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isVendor == true) {
                binding.chipVendor.text = it?.name
                binding.chipVendor.isChecked = true
                binding.chipVendor.visibility = View.VISIBLE
            }
        }

        viewModel.currentCategoryName.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                binding.chipCategory.text = it
                binding.chipCategory.isChecked = true
                binding.chipAmount.visibility = View.VISIBLE
                binding.chipCategory.visibility = View.VISIBLE
                binding.notFind.visibility = View.INVISIBLE
            } else {
                binding.chipCategory.visibility = View.INVISIBLE
                binding.chipAmount.visibility = View.INVISIBLE
                binding.notFind.visibility = View.VISIBLE
            }
        }
    }

    private fun setupNavigationRail() {

        navBar?.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_find -> {
                    if (binding.searchBar.visibility == View.VISIBLE) {
                        binding.searchBar.visibility = View.INVISIBLE
                        navBar?.menu?.get(0)?.setIcon(R.drawable.ic_search_black_24dp)
                    } else {
                        binding.searchBar.visibility = View.VISIBLE
                        navBar?.menu?.get(0)?.setIcon(R.drawable.search_off_24)
                    }
                    true
                }

                R.id.menu_filtr -> {
                    findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToPodborFiltrFragment())
                    true
                }

                R.id.menu_shop -> {
                    if (args.isMasterDoc) {
                        val bundle = bundleOf("podborReturn" to true)
                        val navOptions = NavOptions.Builder()
                            .setPopUpTo(R.id.podborFragment, false)
                            .build()
                        findNavController().navigate(R.id.homeAdapter, bundle, navOptions)
                    } else {
                        findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToRequestDocFragment())
                    }
                    true
                }

                R.id.menu_back -> {
                    findNavController().popBackStack()
                    true
                }

                else -> false
            }
        }
        viewModel.countList.observe(viewLifecycleOwner) {
            val badge = navBar?.getOrCreateBadge(R.id.menu_shop)
            badge?.number = it.toInt()
            badge?.isVisible = it > 0
        }
    }

    private fun setupUI() {
        val adapterAction = AdapterAction({ good, count -> viewModel.addList(good, count) },
            { good -> viewModel.inList(good) },
            { good, count -> viewModel.minList(good, count) },
            { good -> viewModel.deleteFromList(good) },
            { good, update -> showAlertDialog(good, update) },
            { good, update -> showAlertDialogMinus(good, update) })
        val adapter = NewGoodAdapter(adapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        binding.searchRecycler.adapter = adapter
        viewModel.findGoods.observe(viewLifecycleOwner) { listGoodWithStock ->
            adapter.submitList(listGoodWithStock)
        }
        viewModel.countList.observe(viewLifecycleOwner) {
            viewModel.findGoods.value?.let {
                adapter.submitList(viewModel.sync(it))
            }
        }
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.setQuery(binding.searchView.text.toString())
            false
        }

        binding.searchRecycler.addItemDecoration(decoration)

        viewModel.showProgress.observe(viewLifecycleOwner) {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.INVISIBLE
            }
        }
        binding.searchView.addTransitionListener { _, _, newState ->
            if (newState == SearchView.TransitionState.HIDDEN) {
                binding.searchBar.visibility = View.INVISIBLE
                navBar?.menu?.get(0)?.setIcon(R.drawable.ic_search_black_24dp)
            }
        }
    }


    private fun showAlertDialog(
        good: GoodWithStock, update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            viewModel.addList(
                goodView, count
            )
        }
    }

    private fun showAlertDialogMinus(
        good: GoodWithStock, update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            viewModel.addList(
                goodView, if ((good.amount ?: 0.0.minus(count)) > 0) 0.0 else count
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}