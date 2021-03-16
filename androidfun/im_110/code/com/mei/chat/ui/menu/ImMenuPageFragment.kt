package com.mei.chat.ui.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.chat.ui.menu.action.BaseAction
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import kotlinx.android.synthetic.main.im_menu_page_fragment.*

/**
 * Created by zzw on 2019/3/15
 * Des:更多菜单页签
 */
class ImMenuPageFragment : CustomSupportFragment() {

    var data: MutableList<BaseAction> = mutableListOf()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.im_menu_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        im_menu_page_recy.layoutManager = GridLayoutManager(context, 4)
        im_menu_page_recy.adapter = ImMenuPageFragment.ActionAdapter(data)
    }

    class ActionAdapter(data: MutableList<BaseAction>) : BaseQuickAdapter<BaseAction, BaseViewHolder>(R.layout.im_menu_item, data) {
        override fun convert(holder: BaseViewHolder, item: BaseAction) {
            holder.setImageResource(R.id.im_menu_icon, item.iconRes)
            holder.setText(R.id.im_menu_title, item.title)
            holder.itemView.setOnClickListener { item.onClick() }
        }
    }
}
