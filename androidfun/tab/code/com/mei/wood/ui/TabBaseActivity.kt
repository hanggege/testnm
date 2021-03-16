package com.mei.wood.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.inList
import com.mei.orc.tab.TabItemContent
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.view.NewTabItemView
import com.mei.wood.view.OnTabItemSelectedListener
import com.mei.wood.view.TabMenuView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/28
 */
data class TabInfo(val res: Int, val resNor: String, val resPre: String, val animation: String, val changeAnimAssets: String, val title: String, val type: TAB_ITEM)


@Suppress("DEPRECATION")
abstract class TabBaseActivity : MeiCustomActivity(), OnTabItemSelectedListener {

    abstract val contentViews: List<ViewGroup>
    abstract val tabMenuView: TabMenuView
    val tabItems = arrayListOf<TabInfo>()
    val fragmentMap = mutableMapOf<Int, Fragment>()

    abstract fun fragmentForTabItem(item: TabInfo): Fragment

    override fun setContentView(layoutResID: Int) {
        super.setContentView(layoutResID)
        tabMenuView.tabSelectedListener = this
    }

    override fun setContentView(view: View?) {
        super.setContentView(view)
        tabMenuView.tabSelectedListener = this
    }

    override fun onResume() {
        super.onResume()
        fragmentMap[tabMenuView.selectedIndex]?.let { if (it.isAdded) it.userVisibleHint = true }
    }

    override fun onPause() {
        super.onPause()
        fragmentMap[tabMenuView.selectedIndex]?.let { if (it.isAdded) it.userVisibleHint = false }
    }

    /**Activity 被系统杀死后，重新创建时导致以前的fragment重叠问题*/
    @SuppressLint("MissingSuperCall")
    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//        outState.putParcelable("android:support:fragments", null)
    }

    /**
     * 重新创建整个页面
     */
    open fun rebuildPager(tabList: List<TabInfo>, index: Int = 0) {
        supportFragmentManager.commit(allowStateLoss = true) {
            /** 移除以前的fragment **/
            fragmentMap.forEach { remove(it.value) }
            fragmentMap.clear()
        }
        tabItems.clear()
        tabItems.addAll(tabList)
        /** 添加菜单项目 **/
        val menuItems = arrayListOf<NewTabItemView>()
        tabItems.forEach {
            menuItems.add(NewTabItemView(this).apply {
                title = it.title
                imgRes = it.res
                changeAnimAssets = it.changeAnimAssets
                resNor = it.resNor
                resPre = it.resPre
                animation = it.animation
                loadNetIcon()
            })
        }
        tabMenuView.itemViews = menuItems
        tabMenuView.selectedIndex = -1
        /** 选中默认项 **/
        setCurrentTabItem(index)
    }

    fun getCurrentTabItem(): TAB_ITEM = tabItems.getIndexOrNull(getSelectedIndex())?.type
            ?: TAB_ITEM.NONE

    fun getSelectedIndex(): Int = tabMenuView.selectedIndex

    /**
     * 显示指定位置的fragment
     */
    fun setCurrentTabItem(index: Int) {
        if (tabItems.inList(index)) {
            tabMenuView.setCurrentItem(index)
        }
    }

    fun setCurrentTabItem(item: TAB_ITEM) {
        setCurrentTabItem(tabItems.indexOfFirst { it.type == item })
    }

    /****************************[OnTabItemSelectedListener]************************************/
    override fun onInterceptor(index: Int): Boolean = false

    override fun onSelected(index: Int, old: Int) {
        /** 设置选中目标显示 **/
        supportFragmentManager.commit(allowStateLoss = true) {

            /** 我的界面单独用一个View加载Fragment **/
            var fragment = fragmentMap[index]
            val contentView = contentViews[index]
            if (fragment == null) {
                contentView.removeAllViews()
                fragment = fragmentForTabItem(tabItems[index])
                add(contentView.id, fragment)
                fragmentMap[index] = fragment
            }
            show(fragment)
            contentViews.forEachIndexed { i, viewGroup -> viewGroup.isVisible = i == index }
        }
        fragmentMap[index]?.let {
            if (it.isAdded) {
                it.userVisibleHint = true
                (it as? TabItemContent)?.onSelect()
            }
        }
        if (old != index) {//当新老index为同一个时，显示之后就不要再隐藏了
            fragmentMap[old]?.let {
                if (it.isAdded) {
                    it.userVisibleHint = false
                    (it as? TabItemContent)?.onDeSelect()
                }
            }
        }
    }

    override fun onRepeat(index: Int) {
        fragmentMap[index]?.let {
            if (it.isAdded) {
                (it as? TabItemContent)?.onReSelect()
            }
        }
    }
}