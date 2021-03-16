package com.mei.im.ui.view.menu.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.mei.im.ui.view.menu.ChatMenu
import com.mei.im.ui.view.menu.adapter.ChatMoreMenuAdapter
import com.mei.orc.ext.layoutInflaterKtToParent
import com.mei.wood.R
import kotlinx.android.synthetic.main.page_menu.view.*

/**
 *
 * @author Created by lenna on 2020/7/14
 * 目前没有进行分页
 */
class PageMenu @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0)
    : LinearLayout(context, attr, def) {
    var mList = arrayListOf<ChatMenu>()
    private val mMenuAdapter by lazy { ChatMoreMenuAdapter(mList) }
    var mDefSpanCount = 4

    init {
        initView(context)
    }

    private fun initView(context: Context) {
        val view = layoutInflaterKtToParent(R.layout.page_menu)
        addView(view)
        menu_rlv.layoutManager = MenuGridLayoutManager(context, mDefSpanCount)
        menu_rlv.adapter = mMenuAdapter
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun setMenuList(list: List<ChatMenu>?, clickItem: (chatMenu: ChatMenu?) -> Unit = {}) {
        list?.let {
            mList.clear()
            mList.addAll(it)
            mMenuAdapter.notifyDataSetChanged()
            mMenuAdapter.setOnItemClickListener { _, _, position ->
                clickItem(mList[position])
            }
        }
    }
}