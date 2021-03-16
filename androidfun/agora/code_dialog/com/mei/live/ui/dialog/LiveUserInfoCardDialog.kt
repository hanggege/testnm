package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.dialog.showReportDialog
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.ext.shieldingPop
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.room.RoomInfo
import com.net.model.user.DataCard
import com.net.model.user.Medal
import kotlinx.android.synthetic.main.view_live_user_info_card.*


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-06
 * 知心达人用户资料卡
 * @param back type   0:at   1:送礼   2:送礼物加好友
 */
fun FragmentActivity.showLiveUserInfoDialog(roomInfo: RoomInfo, dataCard: DataCard, back: (type: Int, info: DataCard) -> Unit = { _, _ -> }) {
    if (supportFragmentManager.findFragmentByTag("LiveUserInfoCardDialog") == null) {
        LiveUserInfoCardDialog().apply {
            this.back = { back(it, dataCard) }
            this.userInfo = dataCard
            this.mRoomInfo = roomInfo
        }.show(supportFragmentManager, "LiveUserInfoCardDialog")
    }
}

/**
 * 用户或者其他用户资料弹窗
 */
@Suppress("INACCESSIBLE_TYPE")
class LiveUserInfoCardDialog : BottomDialogFragment() {
    var userInfo: DataCard = DataCard()
    var mRoomInfo = RoomInfo()

    var back: (type: Int) -> Unit = {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_live_user_info_card, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (userInfo.userId == 0) {
            dismissAllowingStateLoss()
            return
        }


        //自己看自己
        card_line.isVisible = !userInfo.isSelf
        at_tv.isVisible = !userInfo.isSelf


        left_count.text = userInfo.regTime.orEmpty()
        left_hint.text = "被拉黑${userInfo.blacklistCount}次"
        left_hint.isVisible = userInfo.blacklistCount > 0

        left_layout.isVisible = mRoomInfo.roomRole != 0 && userInfo.roomRole != 2

        center_count.text = userInfo.sendCoinCount.toString()

        right_count.text = userInfo.upstreamCount.toString()

        logo_intimate_man.isVisible = userInfo.isPublisher
        report_user_btn_pub.isVisible = mRoomInfo.roomRole != 0 && !userInfo.isSelf && userInfo.roomRole != 2
        report_user_btn.isVisible = mRoomInfo.roomRole == 0 && !userInfo.isSelf
        card_user_role_ta.isVisible = userInfo.roomRole == 3
        report_user_btn_pub.setOnClickListener {
            //拉黑
            activity?.shieldingPop(this, it, mRoomInfo, userInfo, back)
        }
        level_text.apply {
            isVisible = userInfo.userLevel > 0
            setBackgroundResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Big))
            text = userInfo.userLevel.toString()
            setPadding(0, 0, if (userInfo.userLevel > 9) dip(5) else dip(7), 0)
        }
        dialog_live_room_message_service.apply {
            val model: Medal? = if (mRoomInfo.isStudio) {
                //工作室直播间
                userInfo.medalMap.orEmpty()[mRoomInfo.groupInfo?.groupId.toString()]
            } else {
                userInfo.medalMap.orEmpty()[mRoomInfo.publisherId.toString()]
            }

            isVisible = model != null
            text = model?.medal.orEmpty()
        }
        report_user_btn.setOnClickListener {
            /** 举报 **/
            activity?.showReportDialog(userInfo.userId, mRoomInfo.roomId) {
                if (it == 1) {
                    dismissAllowingStateLoss()
                }
            }
        }

        user_avatar_img.glideDisplay(userInfo.avatar, userInfo.gender.genderAvatar(userInfo.isPublisher))
        user_name_tv.text = userInfo.nickname
        user_name_tv.paint.isFakeBoldText = true
        at_tv.setOnClickListener {
            at()
        }

    }

    //@
    private fun at() {
        back(0)
        dismissAllowingStateLoss()
    }
}