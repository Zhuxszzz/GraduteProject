package com.niantch.graproject.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 *
 * @Author:  Zhuxs - niantchzhu@tencent.com
 * @datetime:  2021
 * @desc:
 */
class TabFragmentAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    var fragments = ArrayList<Fragment>()
    var titles = ArrayList<String>()
    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }


    override fun getCount(): Int {
        return fragments.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return titles[position]
    }
}