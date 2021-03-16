package com.mei.wood.ui.fragment

import com.mei.wood.ui.CustomSupportFragment

/**
 *  Created by zzw on 2019-08-05
 *  Des:新版的懒加载实现基类
 *  1. vp+fragment使用这个必须在初始化FragmentPagerAdapter或者FragmentStatePagerAdapter
 *  传入BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT作为behavior值才有效。
 *  2. FragmentTransaction add使用
FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
fragmentTransaction.add(R.id.xxx,cardFragment);
fragmentTransaction.setMaxLifecycle(cardFragment, Lifecycle.State.RESUME);
fragmentTransaction.commit();
 *
 *  主要原理是利用FragmentTransaction.setMaxLifecycle()
 *  实现fragment的生命周期限定。可参考链接:https://www.jianshu.com/p/85cdc91bfca6
 *
 */

abstract class LazyLoadFragment : CustomSupportFragment() {

    private var isDataLoaded: Boolean = false // 数据是否已请求

    /**
     * 第一次可见时触发调用,此处实现具体的数据请求逻辑
     */
    protected abstract fun lazyLoadData()

    override fun onResume() {
        super.onResume()
        tryLoadData()
    }

    private fun tryLoadData() {
        if (!isDataLoaded) {
            lazyLoadData()
            isDataLoaded = true
        }
    }


}