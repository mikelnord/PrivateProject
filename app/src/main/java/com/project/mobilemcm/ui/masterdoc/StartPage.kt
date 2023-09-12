package com.project.mobilemcm.ui.masterdoc

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentStartPageBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.ui.requestDocument.CompaniesAdapter
import com.project.mobilemcm.util.getContractInfo
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupUI()
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

//        val adapterDiscountCompany = DiscountCompanyAdapter(ArrayList())
//        binding.discontsRecycler.adapter = adapterDiscountCompany
//        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
//        binding.discontsRecycler.addItemDecoration(decoration)
//        viewModel.discountsCompany.observe(viewLifecycleOwner) { listDiscounts ->
//            listDiscounts?.let {
//                adapterDiscountCompany.updateData(it)
//            }
//        }


        viewModelMain.selectedCompanies.observe(viewLifecycleOwner) {
            if (it.id.isNotEmpty()) {
                binding.searchBar.text = it.name
                binding.searchView.hide()
                binding.contractsList.setText("")
                binding.next.isEnabled =
                    (viewModelMain.requestDocument.counterparties_id != "0" && viewModelMain.requestDocument.contract_id.isNotEmpty())

            }
        }

        viewModel.storeList.observe(viewLifecycleOwner) { storeList ->
            storeList?.let { it ->
                val adapter = ArrayAdapter(requireContext(), R.layout.list_item, it)
                binding.storeList.setAdapter(adapter)
                if (viewModelMain.requestDocument.store_id.isNotEmpty()) {
                    val nameStore =
                        it[viewModel.getPositionFromIdStore(viewModelMain.requestDocument.store_id)].name
                    binding.storeList.setText(
                        nameStore,
                        false
                    )
                    viewModelMain.requestDocument.store_name = nameStore
                }
            }
        }

        binding.storeList.setOnItemClickListener { parent, view, position, id ->
            viewModel.getItemFromListStore(position)?.let { viewModelMain.setStoreId(it) }
        }

        viewModelMain.contractsCompanyList.observe(viewLifecycleOwner) { contractListNull ->
            contractListNull?.let { contractList ->
                val adapter =
                    ArrayAdapter(
                        requireContext(),
                        R.layout.list_item,
                        contractList.map { getContractInfo(it) })
                binding.contractsList.setAdapter(adapter)
                if (viewModelMain.requestDocument.contract_id.isNotEmpty()) {
                    val nameContract =
                        contractList[viewModelMain.getPositionFromIdContract(viewModelMain.requestDocument.contract_id)].name
                    binding.contractsList.setText(nameContract, false)
                }
            }
        }

        binding.contractsList.setOnItemClickListener { parent, view, position, id ->
            viewModelMain.getItemFromListContracts(position)
                ?.let {
                    viewModelMain.requestDocument.contract_id = it.id
                    viewModelMain.requestDocument.contract_type = it.type ?: ""
                    binding.next.isEnabled =
                        (viewModelMain.requestDocument.counterparties_id != "0" && viewModelMain.requestDocument.contract_id.isNotEmpty())
                }
        }

        binding.next.setOnClickListener {
            val bundle = bundleOf("isMasterDoc" to true)
            val navOptions = NavOptions.Builder()
                .setPopUpTo(R.id.podborFragment, true)
                .build()
            findNavController().navigate(R.id.podborFragment, bundle, navOptions)
        }

//        viewModel.companyInfo.observe(viewLifecycleOwner) {
//            binding.textDebt.text =
//                if (it?.debt != null) {
//                    "Текущий долг --- ${currencyFormat(it.debt)}"
//                } else "---"
//            binding.textOverdueDebt.text = if (it?.overdue_debt != null) {
//                "Просроченный долг --- ${currencyFormat(it.overdue_debt)}"
//            } else "---"
//            binding.textOverdueDebt5.text = if (it?.overdue_debt5 != null) {
//                "Долг до просрочки 5 дней ---  ${currencyFormat(it.overdue_debt5)}"
//            } else "---"
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
