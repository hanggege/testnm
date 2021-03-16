package com.mei.message.adapter

import android.graphics.Color
import android.text.method.LinkMovementMethod
import android.view.View
import android.view.ViewGroup
import android.webkit.URLUtil
import android.widget.FrameLayout
import android.widget.Space
import android.widget.TextView
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.message.CustomMessage
import com.mei.base.ui.nav.Nav
import com.mei.chat.ui.adapter.item.chatTimeString
import com.mei.live.ext.createSplitSpannable
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.holder.MeiMultiQuickAdapter
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020-02-19
 */

const val NOTIFY_ITEM_MSG = 0
const val NOTIFY_ITEM_DIVIDER = 1
const val NOTIFY_ITEM_HISTORY_TAG = 2
const val NOTIFY_ITEM_EMPTY = 3

class NotifyMessageAdapter(val list: MutableList<Any>) : MeiMultiQuickAdapter<Any, BaseViewHolder>(list), LoadMoreModule {

    override fun getDefItemViewType(position: Int): Int {
        return when (val data = list[position]) {
            is Int -> data
            is CustomMessage -> NOTIFY_ITEM_MSG
            else -> NOTIFY_ITEM_EMPTY
        }
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            NOTIFY_ITEM_HISTORY_TAG -> BaseViewHolder(parent.context.layoutInflaterKt(R.layout.notify_msg_history_item))
            NOTIFY_ITEM_DIVIDER -> BaseViewHolder(FrameLayout(parent.context).apply {
                setPadding(dip(20), 0, dip(20), 0)
                addView(View(context).apply {
                    setBackgroundColor(Color.parseColor("#E8E8E8"))
                    layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, dip(0.75f))
                })
            })
            NOTIFY_ITEM_MSG -> BaseViewHolder(parent.context.layoutInflaterKt(R.layout.notify_msg_adapter_item))
            else -> BaseViewHolder(Space(parent.context))
        }
    }

    override fun convert(holder: BaseViewHolder, item: Any) {
        val msg = item as? CustomMessage
        val info = msg?.customInfo
        val data = info?.data
        if (data is ChickCustomData) {
            holder.setText(R.id.time_stamp_tv, chatTimeString(msg.timMessage.timestamp()))
                    .setText(R.id.notify_title_tv, data.title)
            val userInfo = getCacheUserInfo(data.userId)
            holder.getView<RoundImageView>(R.id.user_avatar_img).glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
            holder.getView<TextView>(R.id.last_msg_content_tv).apply {
                movementMethod = LinkMovementMethod.getInstance()
                text = data.content.createSplitSpannable(Color.parseColor("#999999")) {
                    if (URLUtil.isNetworkUrl(it)) {
                        MeiWebActivityLauncher.startActivity(context, MeiWebData(it, 0))
                    } else {
                        Nav.toAmanLink(context, it)
                    }
                }
            }.setOnClickListener {
                Nav.toAmanLink(context, data.action)
            }
        }
    }
}