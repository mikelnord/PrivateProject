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
import com.google.android.material.search.SearchView
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentPodborBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.ui.goodlist.GoodAdapter
import com.project.mobilemcm.util.showPlusDialog


class PodborFragment : Fragment() {

    private var _binding: FragmentPodborBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()
    private val args: PodborFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPodborBinding.inflate(inflater, container, false)
        setupUI()
        setupFind()
        setupNavigationRail()
        return binding.root
    }

    private fun setupFind() {
        viewModel.setStateRemainder()
        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.chipAmount.isChecked = (viewModel.isStateFilter.value?.isRemainder ?: false) == true
        binding.chipPricegroup.isChecked =
            (viewModel.isStateFilter.value?.isPrisegroup ?: false) == true
        binding.chipVendor.isChecked = (viewModel.isStateFilter.value?.isVendor ?: false) == true
        binding.chipAmount.setOnCheckedChangeListener { _, _ ->
            viewModel.setStateRemainder()
        }
        binding.chipPricegroup.setOnClickListener {
            if (binding.chipPricegroup.text == getString(R.string.pricegroup)) {
                binding.chipPricegroup.isChecked = false
                findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToDialogFiltrNavigation())
            } else viewModel.setStatePricegroup()
        }
        binding.chipVendor.setOnClickListener {
            if (binding.chipVendor.text == getString(R.string.vendor)) {
                binding.chipVendor.isChecked = false
                findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToDialogFiltrNavigation())
            } else viewModel.setStateVendor()
        }

        viewModel.selectedPricegroup.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isPrisegroup == true) {
                binding.chipPricegroup.text = it.name
                binding.chipPricegroup.isChecked = true
            }
        }

        viewModel.selectedVendors.observe(viewLifecycleOwner) {
            if (viewModel.isStateFilter.value?.isVendor == true) {
                binding.chipVendor.text = it.name
                binding.chipVendor.isChecked = true
            }
        }
    }

    private fun setupNavigationRail() {
        binding.navigationRail.headerView?.setOnClickListener {
            if (args.isMasterDoc) {
                val bundle = bundleOf("podborReturn" to true)
                val navOptions = NavOptions.Builder()
                    .setPopUpTo(R.id.homeAdapter, true)
                    .build()
                findNavController().navigate(R.id.homeAdapter, bundle, navOptions)
            } else {
                findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToRequestDocFragment())
            }
        }
        binding.navigationRail.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_find -> {
                    if (binding.searchBar.visibility == View.VISIBLE) {
                        binding.searchBar.visibility = View.INVISIBLE
                        binding.navigationRail.menu[0].setIcon(R.drawable.ic_search_black_24dp)
                    } else {
                        binding.searchBar.visibility = View.VISIBLE
                        binding.navigationRail.menu[0].setIcon(R.drawable.search_off_24)
                    }
                    true
                }

                R.id.menu_filtr -> {
                    findNavController().navigate(PodborFragmentDirections.actionPodborFragmentToDialogFiltrNavigation())
                    true
                }

                R.id.menu_shop -> {
//                    val action = PodborFragmentDirections.actionPodborFragmentToBasketFragment()
//                    findNavController().navigate(action)
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.basketFragment, true)
                        .build()
                    findNavController().navigate(R.id.basketFragment, null, navOptions)
                    true
                }

                R.id.menu_category_list -> {
                    viewModel.showCategoryList()
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
            val badge = binding.navigationRail.getOrCreateBadge(R.id.menu_shop)
            badge.number = it.toInt()
            badge.isVisible = it > 0
        }
    }

    private fun setupUI() {
        val adapterAction = AdapterAction({ good, count -> viewModel.addList(good, count) },
            { good -> viewModel.inList(good) },
            { good, count -> viewModel.minList(good, count) },
            { good -> viewModel.deleteFromList(good) },
            { good, update -> showAlertDialog(good, update) },
            { good, update -> showAlertDialogMinus(good, update) })
        val adapter = GoodAdapter(ArrayList(), adapterAction)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)

        binding.searchRecycler.adapter = adapter
        viewModel.findGoods.observe(viewLifecycleOwner) { listGoodWithStock ->
            adapter.updateData(listGoodWithStock)
        }
        viewModel.countList.observe(viewLifecycleOwner) {
            viewModel.findGoods.value?.let {
                adapter.updateData(viewModel.sync(it))
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
                binding.navigationRail.menu[0].setIcon(R.drawable.ic_search_black_24dp)
                adapter.clearList()
            }
        }
        viewModel.showCategoryList.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentContainerView2.visibility = View.VISIBLE
                viewModel.setCategoryList()
            }
        }
        viewModel.hideCategoryList.observe(viewLifecycleOwner) {
            if (it) {
                binding.fragmentContainerView2.visibility = View.GONE
                viewModel.setHideCategoryList()
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
        viewModel.clearStateFilter()
        _binding = null
    }
}