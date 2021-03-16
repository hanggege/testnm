package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.growingio.android.sdk.collection.GrowingIO
import com.growingio.android.sdk.collection.ImpressionMark
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.base.weight.relative.RoundConstraintLayout
import com.mei.live.ext.parseColor
import com.mei.login.checkLogin
import com.mei.orc.ext.dp
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.net.model.chick.room.RoomList.ScrollingMessage
import com.net.model.chick.room.RoomList.UserGuide.Info
import kotlinx.android.synthetic.main.view_intimate_shuffling_notice.view.*
import org.json.JSONObject

/**
 * IntimateShufflingView
 *
 * 滚动条目
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-02
 */
class IntimateShufflingNoticeView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
        RoundConstraintLayout(context, attrs) {

    private var scrollingMsgList = arrayListOf<Info>()

    //埋点相关
    private var globalId = ""
    private var visibleScale = 0.5f

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_shuffling_notice)

        (getViewById(id) as? RoundConstraintLayout)?.apply {
            delegate?.apply {
                backgroundColor = ContextCompat.getColor(context, R.color.color_f6f6f6)
                cornerRadius = 15
            }
            val paddingValue = 10.dp.toInt()
            setPadding(paddingValue, 0, paddingValue, 0)
        }

        intimate_flipper_notice.flipInterval = (5 * TimeUnit.SECOND).toInt()

        intimate_flipper_notice.inAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (intimate_flipper_notice.isFlipping && JohnUser.getSharedUser().hasLogin()) {
                    val curIndex = intimate_flipper_notice.displayedChild
                    val title = intimate_flipper_notice.currentView.findViewById<TextView>(R.id.item_intimate_scrolling_text).text
                    GrowingIO.getInstance().markViewImpression(ImpressionMark(this@IntimateShufflingNoticeView, "main_gn_entrance_view")
                            .setGlobalId("$globalId-$curIndex")
                            .setVisibleScale(visibleScale)
                            .setVariable(JSONObject().apply {
                                put("screen_name", "首页")
                                put("main_app_gn_type", title)
                                put("main_app_gn_pro", "")
                                put("main_app_gn_label", "")
                            }))
                }
            }

            override fun onAnimationEnd(animation: Animation) {}
            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    fun convertData(scrollingMessage: ScrollingMessage?) {
        scrollingMessage?.apply {
            if (durationSec > 0) intimate_flipper_notice.flipInterval = (durationSec * TimeUnit.SECOND).toInt()
            this@IntimateShufflingNoticeView.visibleScale = visibleScale
            this@IntimateShufflingNoticeView.globalId = globalId
            list?.apply {
                scrollingMsgList.clear()
                scrollingMsgList.addAll(this)
                intimate_flipper_notice.removeAllViews()
                scrollingMsgList.forEach { info ->
                    intimate_flipper_notice.addView(createNoticeView(info))
                }
            }
        }
        if (scrollingMsgList.size > 1) {
            intimate_flipper_notice.startFlipping()
        } else {
            intimate_flipper_notice.stopFlipping()
        }
    }

    private fun createNoticeView(info: Info) = layoutInflaterKt(R.layout.item_intimate_scrolling_msg).apply {
        findViewById<ImageView>(R.id.item_intimate_scrolling_img).glideDisplay(info.icon.orEmpty())
        findViewById<TextView>(R.id.item_intimate_scrolling_text).apply {
            text = info.title
            setTextColor(info.color.parseColor(ContextCompat.getColor(context, R.color.color_FC5D00)))
        }
        setOnClickNotDoubleListener(tag = "item_intimate_scrolling_msg") {
            if (context.checkLogin()) {
                if (info.disappearAfterClick) {
                    removeItemView(info)
                }
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", info.title,
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", "",
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                Nav.toAmanLink(context, info.action)
            }
        }
    }

    private fun removeItemView(info: Info) {
        val curIndex = scrollingMsgList.indexOfFirst { it.title == info.title }
        intimate_flipper_notice.removeViewAt(curIndex)
        scrollingMsgList.removeAt(curIndex)
        if (intimate_flipper_notice.childCount == 1) intimate_flipper_notice.stopFlipping()
    }
}