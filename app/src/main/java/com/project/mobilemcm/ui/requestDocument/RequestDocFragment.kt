package com.project.mobilemcm.ui.requestDocument

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.databinding.FragmentRequestDocBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.goodlist.AdapterAction
import com.project.mobilemcm.util.showAlert
import com.project.mobilemcm.util.showPlusDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class RequestDocFragment : Fragment() {

    private lateinit var binding: FragmentRequestDocBinding
    private val viewModel: CategoryViewModel by activityViewModels()
    private val requestDocViewModel: RequestDocViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestDocBinding.inflate(inflater, container, false)
        setupUI()
        setupNavigationRail()
        setupAdapter()
        return binding.root
    }

    private fun setupAdapter() {
        val adapterAction =
            AdapterAction({ goodWithStock, count -> viewModel.addList(goodWithStock, count) },
                { key -> viewModel.inList(key) },
                { good, count -> viewModel.minList(good, count) },
                { good -> viewModel.deleteFromList(good) },
                { good, update -> showAlertDialog(good, update) },
                { good, update -> showAlertDialogMinus(good, update) })
        val adapter = ListDocRequestAdapter(adapterAction)
        val divider = MaterialDividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL)
        binding.recyclerGoods.addItemDecoration(divider)
        binding.recyclerGoods.adapter = adapter
        viewModel.countList.observe(viewLifecycleOwner) {
            adapter.submitList(viewModel.addStringsList.values.toList())
        }
    }

    private fun setupUI() {
        val adapterCompanies = CompaniesAdapter { counterparties ->
            viewModel.setSelectedCompanies(counterparties)
        }
        binding.searchRecycler.adapter = adapterCompanies
        requestDocViewModel.listCompanies.observe(viewLifecycleOwner) {
            adapterCompanies.submitList(it)
        }

        val adapterCompaniesAdr = CompaniesAdressAdapter {
            viewModel.setSelectedCompaniesAdr(it)
        }
        binding.searchRecyclerAdr.adapter = adapterCompaniesAdr
        viewModel.getCompanyAdres().observe(viewLifecycleOwner) {
            adapterCompaniesAdr.submitList(it)
        }

        binding.searchView.setupWithSearchBar(binding.searchBar)
        binding.searchViewAdr.setupWithSearchBar(binding.searchBarAdr)
        binding.textDate.text = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault())
            .format(viewModel.requestDocument.docDate.time)
        binding.editTextNumber.text = viewModel.requestDocument.document_id.toString()

        viewModel.docSumm.observe(viewLifecycleOwner) {
            binding.textViewSumm.text = String.format("%.2f", it)
        }
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            requestDocViewModel.setQueryCompanies(binding.searchView.text.toString())
            false
        }
        binding.searchViewAdr.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.setQueryCompaniesAdr(binding.searchViewAdr.text.toString())
            false
        }
        viewModel.selectedCompanies.observe(viewLifecycleOwner) {
            it?.let {
                binding.searchBar.text = it.name
                binding.searchView.hide()
                viewModel.requestDocument.counterparties_id = it.id
                viewModel.requestDocument.counterpartiesStores_id = ""
                if (!viewModel.requestDocument.isPickup)
                    binding.searchBarAdr.clearText()
            }
        }
        viewModel.selectedCompaniesAdr.observe(viewLifecycleOwner) {
            it?.let {
                it.address?.let { adres ->
                    binding.searchBarAdr.text = adres
                }
                binding.searchViewAdr.hide()
                viewModel.requestDocument.counterpartiesStores_id = it.id
            }
        }
        if (viewModel.requestDocument.isPickup) {
            binding.searchBarAdr.text = "Самовывоз"
        }
    }

    private fun setupNavigationRail() {
        binding.navigationRail.headerView?.setOnClickListener {
            if (!viewModel.saveDoc()) showAlert(requireContext())
            else findNavController().navigate(RequestDocFragmentDirections.actionRequestDocFragmentToRequestListFragment())
        }
        binding.navigationRail.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_cansel -> {
                    findNavController().navigate(RequestDocFragmentDirections.actionRequestDocFragmentToRequestListFragment())
                    //view?.findNavController()?.popBackStack()
                    true
                }

                R.id.menu_shop -> {
                    //to podborFragment
                    val bundle = bundleOf("isMasterDoc" to false)
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.podborFragment, true)
                        .build()
                    findNavController().navigate(R.id.podborFragment, bundle, navOptions)
                    true
                }

                else -> false
            }
        }
    }


    private fun showAlertDialog(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            viewModel.addList(goodView, count)
        }
    }

    private fun showAlertDialogMinus(
        good: GoodWithStock,
        update: () -> Unit
    ) {
        showPlusDialog(good, requireContext(), update) { goodView, count ->
            good.amount?.let {
                if ((it.minus(count)) < 0) -it else -count
            }?.let {
                viewModel.addList(
                    goodView,
                    it
                )
            }
        }
    }
}