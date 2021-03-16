package com.mei.wood.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mei.wood.ui.CustomSupportFragment

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/28
 */

open class TabItemFragment : CustomSupportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? = View(context)

    @Suppress("DEPRECATION")
    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isAdded) {
            if (isVisibleToUser) willAppear() else willHidden()
        }
    }

    open fun willAppear() {}
    open fun willHidden() {}
}