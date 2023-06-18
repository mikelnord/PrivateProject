package com.project.mobilemcm.ui.masterdoc

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentStartPageBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.requestDocument.CompaniesAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StartPage : Fragment() {

    private var _binding: FragmentStartPageBinding? = null
    private val binding get() = _binding!!
    val viewModel: AdapterViewModel by viewModels()
    private val viewModelMain: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStartPageBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setupUI() {
        binding.searchView.setupWithSearchBar(binding.searchBar)
        viewModel.setOnlyMineStart()
        binding.materialSwitchMy.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setOnlyMine()
        }
        binding.searchView.editText.setOnEditorActionListener { _, _, _ ->
            viewModel.setQueryCompanies(binding.searchView.text.toString())
            false
        }
        val adapterCompanies = CompaniesAdapter { counterparties ->
            viewModelMain.setSelectedCompanies(counterparties)
        }
        binding.searchRecycler.adapter = adapterCompanies
        viewModel.listCompanies.observe(viewLifecycleOwner) {
            adapterCompanies.submitList(it)
        }

        viewModelMain.selectedCompanies.observe(viewLifecycleOwner) {
            binding.searchBar.text = it.name
            binding.searchView.hide()
            viewModelMain.requestDocument.counterparties_id = it.id
            viewModelMain.requestDocument.counterpartiesStores_id = ""
        }

        viewModel.activeUser.observe(viewLifecycleOwner) { loggedInUser ->
            loggedInUser?.let {
                binding.managerTextView.text = "Менеджер: ${it.displayName}"
            }
        }
        viewModel.activeStore.observe(viewLifecycleOwner) {
            binding.skladTextView.text = "Подразделение: $it"
        }

        viewModel.storeList.observe(viewLifecycleOwner) { storeList ->
            storeList?.let {
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
                binding.storeList.setAdapter(adapter)
            }
        }

        binding.storeList.setOnItemClickListener { parent, view, position, id ->
            viewModel.getItemFromListStore(position)?.let { viewModelMain.setStoreId(it) }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}