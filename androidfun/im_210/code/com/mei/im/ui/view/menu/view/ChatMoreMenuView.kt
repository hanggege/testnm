package com.mei.im.ui.view.menu.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import com.mei.im.ui.view.menu.ChatMenu
import com.mei.im.ui.view.menu.MenuPage
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R
import kotlinx.android.synthetic.main.chat_menu.view.*

/**
 *
 * @author Created by lenna on 2020/7/14
 */
class ChatMoreMenuView @JvmOverloads constructor(context: Context, attr: AttributeSet? = null, def: Int = 0)
    : LinearLayout(context, attr, def) {
    var mClickItem: (chatMenu: ChatMenu?) -> Unit = {}
    var mMaxPageSize: Int = 8
    var menuPageList = arrayListOf<MenuPage>()

    init {
        initView()
    }

    private fun initView() {
        val view = layoutInflaterKt(R.layout.chat_menu)
        addView(view)
        menu_pager.adapter = object : Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val contentView = layoutInflaterKt(R.layout.item_page_menu)
                contentView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                return object : ViewHolder(contentView) {}
            }

            override fun getItemCount(): Int {
                return menuPageList.size
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                holder.itemView.findViewById<PageMenu>(R.id.page_menu).setMenuList(menuPageList[position].data, mClickItem)
            }
        }
        menu_pager.registerOnPageChangeCallback(object : OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                menu_indicator_view.selectIndex(position)
            }
        })
    }

    fun setMenuList(menuList: List<ChatMenu>
                    , clickItem: (chatMenu: ChatMenu?) -> Unit = {}) {
        mClickItem = clickItem
        loadMenuData(menuList)
    }

    private fun loadMenuData(menuList: List<ChatMenu>) {
        var list = arrayListOf<ChatMenu>()
        val listSize = menuList.size
        menuPageList.clear()
        if (listSize > mMaxPageSize) {
            // 每页进行均分，不足一页的也划为一页
            for (i in 0 until listSize) {
                list.add(menuList[i])
                if (((i + 1) % mMaxPageSize == 0) || ((i + 1) == listSize)) {
                    val menuPage = MenuPage()
                    menuPage.data = list
                    menuPageList.add(menuPage)
                    list = arrayListOf()

                }
            }
        } else {
            val menuPage = MenuPage()
            menuPage.data = menuList
            menuPageList.add(menuPage)
        }
        menu_pager.adapter?.notifyDataSetChanged()
        menu_indicator_view.buildIndicator(menuPageList.size, R.drawable.chat_menu_indicator_bg, dip(5))
    }

}