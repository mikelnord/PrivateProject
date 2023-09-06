package com.project.mobilemcm.ui.categorylist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DividerItemDecoration
import com.project.mobilemcm.databinding.FragmentCategoryListBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class CategoryListFragment : Fragment() {

    private var _binding:FragmentCategoryListBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val adapter = CategoryAdapter(
            ArrayList(),
            { id, name -> viewModel.setCurrentCategory(id, name) },
            viewModel.currentCategory)
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(decoration)
        viewModel.categoryChildList.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}