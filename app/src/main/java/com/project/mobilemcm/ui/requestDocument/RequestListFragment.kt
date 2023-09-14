package com.project.mobilemcm.ui.requestDocument

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.navigation.NavigationBarView
import com.project.mobilemcm.R
import com.project.mobilemcm.data.local.database.model.GoodWithStock
import com.project.mobilemcm.data.local.database.model.Result
import com.project.mobilemcm.databinding.FragmentRequestListDocBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import com.project.mobilemcm.util.showPlusDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RequestListFragment : Fragment() {

    private lateinit var binding: FragmentRequestListDocBinding
    private val viewModel: RequestListViewModel by viewModels()
    private val activityViewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRequestListDocBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val adapter = RequestAdapter({ id -> activityViewModel.openDoc(id) },
            { idDoc, email -> showEmailDialog(idDoc, email) })

        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.addItemDecoration(decoration)
        viewModel.docList.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
        activityViewModel.goDoc.observe(viewLifecycleOwner) {
            if (it) {
                activityViewModel.setgoDoc()
                findNavController()
                    .navigate(RequestListFragmentDirections.actionRequestListFragmentToRequestDocFragment())
            }
        }
        val navBar = binding.root.findViewById<NavigationBarView>(R.id.navigation_rail)
        navBar.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menu_home -> {
                    findNavController()
                        .navigate(RequestListFragmentDirections.actionRequestListFragmentToHomeFragment())
                    true
                }

                R.id.menu_send -> {
                    viewModel.sendDocument()
                    true
                }

                R.id.menu_add -> {
                    activityViewModel.clearDoc()
                    findNavController().navigate(R.id.homeAdapter)
                    true
                }

                else -> false
            }
        }

        viewModel.resultEmail.observe(viewLifecycleOwner) {
            if (it.status == Result.Status.SUCCESS) {
                Toast.makeText(requireContext(), "Email send", Toast.LENGTH_SHORT).show()
            }
            if (it.status == Result.Status.ERROR) {
                Toast.makeText(requireContext(), "Email send Error!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showEmailDialog(
        docId: String,
        email: String
    ) {
        viewModel.showEmailDialog(requireContext(), docId, email)
    }

}