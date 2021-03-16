package com.mei.live.ui.dialog.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.dialog.showReportDialog
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showSendGiftDialog
import com.mei.live.ui.ext.UserToMentorOptionsType
import com.mei.live.ui.ext.shieldingDialog
import com.mei.live.ui.ext.showUserToShieldingDialog
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.live.views.followFriend
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.room.RoomInfo
import com.net.model.rose.RoseFromSceneEnum
import com.net.model.user.DataCard
import kotlinx.android.synthetic.main.fragment_no_work_use_see_pub_card.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.fragment
 * @ClassName:      UserToAnchorCardFragment
 * @Description:    用户查看主播资料卡，这个资料卡没有作品没有简介
 * @Author:         zxj
 * @CreateDate:     2020/6/10 16:14
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 16:14
 * @UpdateRemark:
 * @Version:
 */
class UserToAnchorCardFragment : CustomSupportFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_no_work_use_see_pub_card, container, false)

    var userInfo: DataCard = DataCard()

    //关闭窗口回调
    var dismissBack: () -> Unit = {}
    var roomInfo: RoomInfo = RoomInfo()
    var back: (type: Int) -> Unit = {}


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (userInfo.isSelf) {
            /**主播自己看自己*/
            follow_anchor.isVisible = false
            card_bottom_action_layout.isVisible = false
            bottom_line.isInvisible = true
            more_btn.isVisible = false
        } else {
            follow_anchor.isVisible = !userInfo.isFollow
            card_bottom_action_layout.isVisible = true
            bottom_line.isVisible = true
            more_btn.isVisible = true
        }
        val interestStr = userInfo.skills.orEmpty().joinToString(",") { it.name }
        user_info_desc_tv.text = "擅长领域:$interestStr"
        user_info_desc_tv.isVisible = userInfo.skills.orEmpty().isNotEmpty()
        user_avatar_img.glideDisplay(userInfo.avatar, userInfo.gender.genderAvatar(userInfo.isPublisher))
        anchor_name.text = userInfo.nickname
        anchor_name.paint.isFakeBoldText = true
        if (userInfo.gender == 1) {
            user_extra_tv.text = "男 ${userInfo.birthYear.orEmpty()}"
            user_extra_tv.setBackColor(Color.parseColor("#6995ff"))
        } else {
            user_extra_tv.text = "女 ${userInfo.birthYear.orEmpty()}"
            user_extra_tv.setBackColor(Color.parseColor("#FF69B0"))
        }

        left_count.text = userInfo.receivedCoinCount.toString()
        center_count.text = userInfo.fansCount.toString()
        right_count.text = changTVsize(userInfo.broadcastCount.orEmpty())
        listener()
    }

    /**
     * 小时和分钟缩小
     */
    private fun changTVsize(value: String): CharSequence? {
        return SpannableString(value).apply {
            if (contains("小时")) {
                setSpan(RelativeSizeSpan(0.6f), value.indexOf("小"), value.indexOf("时") + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
            if (contains("分钟")) {
                setSpan(RelativeSizeSpan(0.6f), value.indexOf("分"), value.indexOf("钟") + 1, Spanned.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
    }

    //@TA
    private fun at() {
        back(0)
        dismissBack()
        growCard("@TA")
    }

    //送礼物
    private fun sendGift() {
        back(1)
        activity?.showSendGiftDialog(userInfo.userId, userInfo.nickname.orEmpty(), (activity as? VideoLiveRoomActivity)?.roomId.orEmpty(), roseFromScene = RoseFromSceneEnum.COMMON_ROOM)
        dismissBack()
        growCard("送礼")
    }

    /**
     * 埋点
     */
    private fun growCard(clickType: String) {
        val roomType = roomInfo.getRoomTypeForGrowingTrack()
        GrowingUtil.track("personal_datacard_click", "room_id", roomInfo.roomId, "user_id", roomInfo.createUser?.userId.toString(), "room_type", roomType, "click_type", clickType, "view_type", "无简介无作品")
    }

    /**
     * 事件操作
     */
    private fun listener() {
        //关注或者私聊
        follow_anchor.setOnClickListener {
            /**关注*/
            growCard("关注")
            (activity as? MeiCustomActivity)?.followFriend(userInfo.userId, 1, (activity as? VideoLiveRoomActivity)?.roomId
                    ?: "") {
                if (it) {
                    dismissBack()
                }
            }
        }
        //@ TA
        at_tv.setOnClickListener {
            at()
        }
        //送礼
        send_gift.setOnClickListener {
            sendGift()
        }
        //用户操作菜单
        more_btn.setOnClickListener {

            activity?.showUserToShieldingDialog(roomInfo, more_btn, back = { type ->
                when (type) {
                    // 直播间举报
                    UserToMentorOptionsType.REPORT.name -> {
                        activity?.showReportDialog(userInfo.userId, roomInfo.roomId) {
                            if (it == 1) {
                                dismissBack()
                            }
                        }
                    }
                    UserToMentorOptionsType.SHIELDING.name -> {
                        activity?.shieldingDialog("是否确定拉黑 ${userInfo.nickname.orEmpty()}?", userInfo.userId.toString(), roomInfo.roomId, back, dialogMiss = {
                            //拉黑成功退出直播间
                            if (roomInfo.createUser?.userId == userInfo.userId) {
                                activity?.finish()
                            }
                        })
                    }
                }
            })
        }
        //跳转个人主页
        user_avatar_img.setOnClickListener {
            growCard("主播头像")
            Nav.toPersonalPage(activity, userInfo.userId)
        }

    }
}