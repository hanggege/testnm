package com.mei.chat.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mei.chat.ui.menu.action.BaseAction
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import kotlinx.android.synthetic.main.im_menu_fragment.*


/**
 * Created by zzw on 2019/3/15
 * Des:更多菜单
 */
class ImMenuFragment : CustomSupportFragment() {

    var data: MutableList<BaseAction> = mutableListOf()


    fun <T : BaseAction> removeAction(vararg list: Class<T>) {
        var isRemove = false
        list.forEach { actionCls ->
            isRemove = data.removeAll { it::class.java.name == actionCls.name }
        }
        if (isRemove) {
            setData()
        }
    }


    fun <T : BaseAction> addActions(actions: List<T>) {
        actions.forEach { action ->
            data.removeAll { it::class.java.name == action::class.java.name }
        }
        data.addAll(actions)
        setData()
    }


    fun <T : BaseAction> addAction(action: T) {
        addActions(arrayListOf(action))
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.im_menu_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //这里内层如果用list的话，那么下面data赋值会报错，所以这里用的array，然后用array.asList()生成一个新的对象

        setData()
        menu_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                menu_page_indicator_view.selectIndex(position)
            }
        })
    }

    private val pageNum = 8

    private fun setData() {
        //判断一下 不然在fragment没有attach到activity的时候，调用函数出现空指针，出现不必要的一些资源浪费操作
        if (menu_page_indicator_view == null) return

        val pageList: MutableList<Array<BaseAction>> = mutableListOf()
        val pages = data.size / pageNum + if (data.size % pageNum > 0) 1 else 0

        //设置序号
        for ((index, value) in data.withIndex()) {
            value.index = index
            value.fragment = this
        }

        for (i in 0 until pages) {
            val element = data.subList(i * pageNum, Math.min((i + 1) * pageNum, data.size))
//            val element = Arrays.copyOfRange<BaseAction>(data, i * pageNum, Math.min((i + 1) * pageNum, data.size))
            pageList.add(element.toTypedArray())
        }

        val fragments = mutableListOf<ImMenuPageFragment>()

        for (value in pageList) {
            //设置数据
            fragments.add(ImMenuPageFragment().apply {
                data = value.toMutableList()
            })
        }
        menu_page_indicator_view.selectIndex(pages)
        if (pages == 1) {
            menu_page_indicator_view?.visibility = GONE
        } else {
            menu_page_indicator_view?.visibility = View.VISIBLE
        }

        menu_pager2.adapter = MenusPagerAdapter(this, fragments)

        menu_pager2.forEach {
            if (it is RecyclerView) {
                it.overScrollMode = View.OVER_SCROLL_NEVER
            }
        }
    }


    private class MenusPagerAdapter(fm: Fragment, private val fragments: List<Fragment>)
        : FragmentStateAdapter(fm) {
        override fun createFragment(position: Int): Fragment = fragments[position]

        override fun getItemCount() = fragments.size

    }


}
