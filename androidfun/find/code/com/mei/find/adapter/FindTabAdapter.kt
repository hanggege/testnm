@file:Suppress("DEPRECATION")

package com.mei.find.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentStatePagerAdapter

/**
 * TabAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-22
 */
class FindTabAdapter1(activity: FragmentActivity, val fragments: List<Fragment>) :
        FragmentStatePagerAdapter(activity.supportFragmentManager) {


    override fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    override fun getCount(): Int {
        return fragments.size
    }

}