package com.project.mobilemcm.ui.masterdoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.mobilemcm.R
import com.project.mobilemcm.databinding.FragmentHomeAdapterBinding
import com.project.mobilemcm.ui.categorylist.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeAdapter : Fragment() {

    private var _binding: FragmentHomeAdapterBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private val args: HomeAdapterArgs by navArgs()
    private val viewModelMain: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeAdapterBinding.inflate(inflater, container, false)
        setupUI()
        return binding.root
    }

    private fun setupUI() {
        val tabLayout = binding.tabs
        viewPager = binding.viewPager
        viewPager.adapter = PageAdapter(this)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.setIcon(getTabIcon(position))
            tab.text = getTabTitle(position)
            tab.view.isClickable = false
        }.attach()
        if (args.podborReturn) viewPager.currentItem = 1
        binding.viewPager.isUserInputEnabled = false
        //  setTabLayout(tabLayout)
        viewModelMain.getActiveUser().observe(viewLifecycleOwner) { loggedInUser ->
            loggedInUser?.let {
                binding.managerTextView.text = it.displayName
                if (it.division_id == "c3a21002-ef22-11e5-a605-f07959941a7c" && viewModelMain.requestDocument.store_id.isEmpty()) {
                    viewModelMain.requestDocument.store_id = "ac7265a0-66bb-11df-b7ab-001517890160"
                }
            }
        }
    }

    private fun setTabLayout(tabLayout: TabLayout) {
        viewModelMain.selectedCompanies.observe(viewLifecycleOwner) { company ->
            if (company.id.isNotEmpty()) {
                tabLayout.getTabAt(1)?.let {
                    it.view.isClickable = true
                }
                tabLayout.getTabAt(0)?.let {
                    it.view.isClickable = true
                }
            } else {
                tabLayout.getTabAt(1)?.let {
                    it.view.isClickable = false
                }
                tabLayout.getTabAt(2)?.let {
                    it.view.isClickable = false
                }
            }
        }
        viewModelMain.countList.observe(viewLifecycleOwner) { count ->
            if (count != 0) {
                tabLayout.getTabAt(2)?.let {
                    it.view.isClickable = true
                }
            } else {
                tabLayout.getTabAt(2)?.let {
                    it.view.isClickable = false
                }
            }
        }
    }

    private fun getTabIcon(position: Int): Int {
        return when (position) {
            END_PAGE_INDEX -> R.drawable.ic_end_selector
            BASKET_PAGE_INDEX -> R.drawable.ic_basket_selector
            START_PAGE_INDEX -> R.drawable.ic_start_selector
            else -> throw IndexOutOfBoundsException()
        }
    }

    private fun getTabTitle(position: Int): String? {
        return when (position) {
            END_PAGE_INDEX -> getString(R.string.end_page_title)
            BASKET_PAGE_INDEX -> getString(R.string.basket_page_title)
            START_PAGE_INDEX -> getString(R.string.start_page_title)
            else -> null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}