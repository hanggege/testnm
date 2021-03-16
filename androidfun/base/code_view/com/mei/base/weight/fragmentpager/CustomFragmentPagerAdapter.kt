@file:Suppress("DEPRECATION")

package com.mei.base.weight.fragmentpager

import android.os.Bundle
import android.os.Parcelable
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.mei.wood.ui.CustomSupportFragment
import java.util.*

/**
 * Created by LingYun on 2017/6/14.
 *
 */

class CustomFragmentPagerAdapter(fm: FragmentManager)
    : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragments = ArrayList<Fragment>()                         //fragment列表
    private val mTitles = ArrayList<String>()                              //tab名的列表

    var curFragment: Fragment? = null
        private set

    override fun getItemId(position: Int): Long {
        return mFragments[position].hashCode().toLong()
    }

    override fun getItem(position: Int): Fragment {
        return mFragments[position]
    }

    override fun getCount(): Int {
        return mTitles.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mTitles[position % mTitles.size]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

    override fun setPrimaryItem(container: ViewGroup, position: Int, `object`: Any) {
        curFragment = `object` as CustomSupportFragment
        super.setPrimaryItem(container, position, `object`)
    }

    fun setFragmentsAndTitles(fragmentList: List<CustomSupportFragment>, titleList: List<String>) {
        mFragments.clear()
        mTitles.clear()
        mFragments.addAll(fragmentList)
        mTitles.addAll(titleList)
        notifyDataSetChanged()
    }

}
