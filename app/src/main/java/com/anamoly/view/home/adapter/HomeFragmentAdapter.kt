package com.mel3qa.business.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.anamoly.view.home.fragment.HomeFragment
import com.anamoly.view.home.fragment.HomeProductFragment

class HomeFragmentAdapter(
    fm: FragmentManager,
    val isRtl: Boolean
) : FragmentStatePagerAdapter(fm) {

    override fun getItem(pos: Int): Fragment {
        var position = pos
        if (isRtl) {
            position = count - position - 1
        }
        // return fragment and pass data
        return HomeProductFragment().newInstance(position)
    }

    override fun getCount(): Int {
        return HomeFragment.homeProductTitleList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return HomeFragment.homeProductTitleList[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}
