package com.project.mobilemcm.ui.formpodbor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentDialogFiltrBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel

class DialogPodborFiltr : DialogFragment(R.layout.fragment_dialog_filtr) {

    private var _binding: FragmentDialogFiltrBinding? = null
    private val binding get() = _binding!!
    private val viewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogFiltrBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {

        binding.elevatedButton.setOnClickListener {
            dismiss()
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}