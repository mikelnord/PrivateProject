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

    private lateinit var binding: FragmentCategoryListBinding
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val adapter = CategoryAdapter(
            ArrayList(),
            { id -> viewModel.setCurrentCategory(id) },
            { head->viewModel.hideCategoryList() })
        val decoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recycler.adapter = adapter
        binding.recycler.addItemDecoration(decoration)
        viewModel.categoryChildList.observe(viewLifecycleOwner) {
            adapter.updateData(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
    }

}