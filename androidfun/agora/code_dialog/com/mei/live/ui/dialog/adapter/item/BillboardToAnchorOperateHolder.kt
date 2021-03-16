package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.mei.base.ui.nav.Nav
import com.mei.im.ui.dialog.showChatDialog
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R

/**
 * Created by hang on 2020/9/9.
 */
class BillboardToAnchorOperateHolder(itemView: View) : CardBaseHolder(itemView) {

    override fun refresh(item: Any) {
        (item as? BillboardOperateWrapper)?.let {
            val userId = it.userId
            getView<LinearLayout>(R.id.chat_to_anchor).setOnClickNotDoubleListener {
                (itemView.context as? FragmentActivity)?.showChatDialog(userId.toString(),"BillboardDataCard")
            }

            getView<TextView>(R.id.look_page).setOnClickNotDoubleListener {
                Nav.toPersonalPage(itemView.context, userId)
            }
        }

    }
}

data class BillboardOperateWrapper(val userId: Int)