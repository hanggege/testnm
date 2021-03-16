package com.mei.live.ui.dialog.fragment

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.mei.base.ui.nav.Nav
import com.mei.dialog.showReportDialog
import com.mei.im.ui.dialog.showChatDialog
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showSendGiftDialog
import com.mei.live.ui.ext.shieldingDialog
import com.mei.live.views.followDelete
import com.mei.live.views.followFriend
import com.mei.orc.ext.dip
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.chick.tab.isShowShieldingBtn
import com.net.model.room.RoomType
import com.net.model.rose.RoseFromSceneEnum
import kotlinx.android.synthetic.main.fragment_user_to_presenter_card.*

/**
 * Created by hang on 2020/7/27.
 */
fun UserToPresenterCardFragment.initListener() {
    recycler_view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            if (type == 0 && isAdded) {
                scrollY += dy
                if (scrollY <= dip(50)) {
                    println("percent:${scrollY.toFloat() / dip(50)}")
                    doHeadScrollAnim(scrollY.toFloat() / dip(50))
                } else {
                    doHeadScrollAnim(1f)
                }
            }
        }
    })

    presenter_avatar.setOnClickNotDoubleListener {
        growCard("主播头像")
        Nav.toPersonalPage(activity, dataCard.memberInfo?.userId ?: 0)
    }

    add_follow_layout.setOnClickListener {
        if (dataCard.isFollow) {
            growCard("私聊")
            activity.showChatDialog(dataCard.memberInfo?.userId.toString(), "StudioDataCard")
            dismissBack()
        } else {
            growCard("关注")
            (activity as? MeiCustomActivity)?.followFriend(dataCard.memberInfo?.userId
                    ?: 0, 1, roomInfo.roomId) {
                updateFollowUi(it)
            }
        }
    }
    follow_presenter.setOnClickListener {
        add_follow_layout.performClick()
    }

    send_gift.setOnClickListener {
        growCard("送礼")
        activity?.showSendGiftDialog(dataCard.memberInfo?.userId
                ?: 0, dataCard.memberInfo?.nickname.orEmpty(), roomInfo.roomId,
                if (roomInfo.roomType == RoomType.COMMON) RoseFromSceneEnum.COMMON_ROOM else RoseFromSceneEnum.EXCLUSIVE_ROOM, fromType = "主播资料卡送礼")
    }

    at_tv.setOnClickListener {
        growCard("@TA")
        getCacheUserInfo(dataCard.memberInfo?.userId ?: 0)?.let {
            (activity as? VideoLiveRoomActivity)?.showInputKey(it)
        }
        dismissBack()
    }

    at_presenter.setOnClickListener {
        at_tv.performClick()
    }

    report_presenter.setOnClickNotDoubleListener {
        /** 举报 **/
        activity?.showReportDialog(dataCard.memberInfo?.userId ?: 0, roomInfo.roomId) {
            if (it == 1) {
                dismissBack()
            }
        }
    }

    menu_manager.setOnClickNotDoubleListener {
        showManagerPop()
    }
}

fun UserToPresenterCardFragment.showManagerPop() {
    val popHeight = if (isShowShieldingBtn(type == 0 || type == 1) && dataCard.isFollow) {
        dip(140)
    } else if (isShowShieldingBtn(MeiUser.getSharedUser().info?.userId != roomInfo.createUser?.userId) || dataCard.isFollow) {
        dip(110)
    } else {
        dip(80)
    }
    managerPopWindow = CommonPopupWindow.Builder(activity).setView(R.layout.layout_pop_manager)
            .setWidthAndHeight(dip(130), popHeight)
            .setViewOnclickListener { view, _ ->
                view.findViewById<TextView>(R.id.report).apply {
                    setOnClickListener {
                        managerPopWindow?.dismiss()
                        activity.showReportDialog(dataCard.memberInfo?.userId ?: 0, roomInfo.roomId)
                    }
                }
                view.findViewById<TextView>(R.id.cancel_follow).apply {
                    isVisible = dataCard.isFollow == true
                    setOnClickListener {
                        managerPopWindow?.dismiss()

                        (activity as? MeiCustomBarActivity)?.followDelete(dataCard.memberInfo?.userId
                                ?: 0) {
                            updateFollowUi(!it)
                        }
                    }
                }
                view.findViewById<TextView>(R.id.live_shield_tv).apply {
                    isVisible = isShowShieldingBtn(type == 0 || type == 1)
                    setOnClickListener {
                        managerPopWindow?.dismiss()
                        activity?.shieldingDialog("是否确定拉黑 ${dataCard.nickname.orEmpty()}?", dataCard.memberInfo?.userId.toString(), roomInfo.roomId, back, dialogMiss = {
                            //拉黑成功退出直播间
                            activity?.finish()
                            dismissBack()
                        })
                    }
                }
            }
            .setAnimationStyle(R.style.AnimDown).create()
    managerPopWindow?.showAsDropDown(menu_manager, -dip(60), -dip(5))
}

fun UserToPresenterCardFragment.doHeadScrollAnim(percent: Float) {
    presenter_avatar.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = dip(15) - (dip(5) * percent).toInt()
        marginStart = dip(22) - (dip(12) * percent).toInt()
        width = dip(55) - (dip(15) * percent).toInt()
        height = dip(55) - (dip(15) * percent).toInt()
    }
    presenter_name.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        topMargin = ((dip(40) - getNameHeight()) * percent / 2).toInt()
        marginStart = (dip(22) - dip(12) * percent).toInt()
    }
    icon_presenter_layout.apply {
        updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = dip(41) - (dip(13) * percent).toInt()
        }
        scaleX = 1 - 0.2f * percent
        scaleY = 1 - 0.2f * percent
    }

    add_follow_layout.alpha = 1 - percent
    send_gift.alpha = 1 - percent
    at_tv.alpha = 1 - percent

    follow_presenter.isVisible = false
    operate_group.isVisible = type == 0
    at_presenter.isVisible = type == 1 || type == 3
    switchMenuManagerVisible(percent)

    if (percent < 0.1f) {
        at_presenter.isVisible = false
        operate_group.isVisible = type == 0
//        report_presenter.isVisible = type != 2
    }
    if (percent > 0.9) {
        follow_presenter.isVisible = type == 0
        at_presenter.isVisible = type == 1 || type == 3
        operate_group.isVisible = false
//        report_presenter.isVisible = type != 2 && type != 0
    }
}

fun UserToPresenterCardFragment.switchMenuManagerVisible(percent: Float) {
    menu_manager.isVisible = (type == 0 && percent < 0.1f) || type == 1
    report_presenter.isVisible = type != 2 && type != 0 && type != 1
}

fun UserToPresenterCardFragment.updateFollowUi(isFollow: Boolean) {
    dataCard.isFollow = isFollow
    add_follow.text = if (isFollow) "私聊" else "关注"
    follow_presenter.text = if (isFollow) "私聊" else "关注"
    add_follow_img.setImageResource(if (isFollow) R.drawable.icon_chat_tip else R.drawable.icon_follow_add)
    if (type == 0) {
        doHeadScrollAnim(scrollY.toFloat() / dip(50))
    }
}

private fun UserToPresenterCardFragment.getNameHeight(): Int {
    val fontMetrics = presenter_name.paint.fontMetricsInt
    return fontMetrics.bottom - fontMetrics.top
}