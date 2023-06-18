package com.project.mobilemcm.ui.masterdoc

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val END_PAGE_INDEX = 2
const val BASKET_PAGE_INDEX = 1
const val START_PAGE_INDEX = 0

class PageAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val tabFragmentsCreators: Map<Int, () -> Fragment> = mapOf(
        START_PAGE_INDEX to { StartPage() },
        BASKET_PAGE_INDEX to { BasketFragmentMasterDoc() },
        END_PAGE_INDEX to { EndPage() }
    )

    override fun getItemCount() = tabFragmentsCreators.size

    override fun createFragment(position: Int): Fragment {
        return tabFragmentsCreators[position]?.invoke() ?: throw IndexOutOfBoundsException()
    }

}