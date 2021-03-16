package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.dialog.showReportDialog
import com.mei.init.spiceHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.adapter.UserInfoCardCouponAdapter
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.ext.shieldingPop
import com.mei.live.ui.fragment.inviteUpStream
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.room.CouponLabelInfo
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomInfo
import com.net.model.room.RoomType
import com.net.model.user.DataCard
import com.net.network.chick.user.DataCardRequest
import com.net.network.chick.user.PublisherCardForRankRequest
import kotlinx.android.synthetic.main.view_user_info_card.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-06
 * 用户资料卡
 * @param back type   0:at   1:送礼   2:送礼物加好友
 */
fun FragmentActivity?.showUserInfoDialog(roomInfo: RoomInfo, userId: Int, back: (type: Int, info: DataCard) -> Unit = { _, _ -> }) {
    //殷浩说的1000以下的用户没有资料卡
    if (userId < 1000) return
    (this as? MeiCustomBarActivity)?.run {
        showLoading(true)
        apiSpiceMgr.executeToastKt(DataCardRequest(roomInfo.roomId, userId), success = {
            if (it.isSuccess && it.data != null && !isFinishing) {
                val result = it.data
                if ((roomInfo.isCreator || roomInfo.groupRole > 0) && result.groupId <= 0 && !result.isSelf) {
                    /**主播看其他用户 或者 工作室成员看游客*/
                    showUserInfoDialogs(roomInfo, result, back)
                } else {
                    spiceHolder().apiSpiceMgr.requestUserInfo(arrayOf(userId))
                    if (roomInfo.createUser?.userId == userId || result.groupId > 0) {
                        /**其他用户看主播 或者 工作室的其他成员*/
                        showUserToAnchorCardDialog(roomInfo, result, "", back)
                    } else {
                        /**其他用户看非主播*/
                        showLiveUserInfoDialog(roomInfo, result, back)
                    }
                }
            }
        }, finish = {
            showLoading(false)
        })
    }
}

const val DATA_CARD_FROM_BILLBOARD = "data_card_from_billboard"
fun FragmentActivity?.showRankUserInfoDialog(userId: Int, back: (type: Int, info: DataCard) -> Unit = { _, _ -> }) {
    (this as? VideoLiveRoomActivity)?.run {
        if (userId == roomInfo.createUser?.userId) {
            showUserInfoDialog(roomInfo, userId, back)
        } else {
            showLoading(true)
            apiSpiceMgr.executeToastKt(PublisherCardForRankRequest(userId), success = {
                if (it.isSuccess && it.data != null && !isFinishing) {
                    showUserToAnchorCardDialog(roomInfo, it.data, DATA_CARD_FROM_BILLBOARD, back)
                }
            }, finish = { showLoading(false) })
        }
    }
}

private fun FragmentActivity.showUserInfoDialogs(roomInfo: RoomInfo, dataCard: DataCard, back: (type: Int, info: DataCard) -> Unit = { _, _ -> }) {
    if (!this.isFinishing && supportFragmentManager.findFragmentByTag("UserInfoCardDialog") == null) {
        UserInfoCardDialog().apply {
            this.back = { back(it, dataCard) }
            this.userInfo = dataCard
            this.roomInfo = roomInfo
        }.show(supportFragmentManager, "UserInfoCardDialog")
    }
}

/**
 * 用户资料弹窗
 */
class UserInfoCardDialog : BottomDialogFragment() {
    var userInfo: DataCard = DataCard()
    var roomInfo: RoomInfo = RoomInfo()
    var roomId = ""
    var back: (type: Int) -> Unit = {}

    private val couponList = arrayListOf<CouponLabelInfo>()
    private val couponAdapter by lazy { UserInfoCardCouponAdapter(couponList) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_user_info_card, container, false)
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
        roomId = roomInfo.roomId

//        •主播查看用户的资料卡显示3个专属标签
//        • 第一个显示为自己的专属服务，第二三个显示用户最新购买的
//        • 用户端看用户资料卡不需要展示

        val mySelf = if (roomInfo.isStudio) {
            userInfo.medalMap.orEmpty()[roomInfo.groupInfo?.groupId.toString()]
        } else {
            userInfo.medalMap.orEmpty()[JohnUser.getSharedUser().userIDAsString]
        }
        service_representation_name_one.apply {
            isVisible = mySelf != null && !userInfo.isPublisher
            text = mySelf?.medal.orEmpty()
        }
        card_user_role_ta.isVisible = userInfo.roomRole == 3
        logo_intimate_man.isVisible = userInfo.isPublisher


        left_count.text = userInfo.regTime.orEmpty()
        left_hint.text = "被拉黑${userInfo.blacklistCount}次"
        left_hint.isVisible = userInfo.blacklistCount > 0

        center_count.text = userInfo.sendCoinCount.toString()
        val sendCoinRex = if (roomInfo.isCommonRoom) "送我心币" else "送工作室心币"
        center_hint.text = sendCoinRex + userInfo.sendMeCount
        center_hint.isVisible = userInfo.sendMeCount > 0

        right_count.text = userInfo.upstreamCount.toString()
        val upstreamCountRex = if (roomInfo.isCommonRoom) "与我连线" else "与工作室连线"
        right_hint.text = "${upstreamCountRex + userInfo.linkedMeTimes}次"
        right_hint.isVisible = userInfo.linkedMeTimes > 0

        report_user_btn_pub.isVisible = (roomInfo.isCreator || (roomInfo.groupRights and 1 != 0 && roomInfo.isStudio) || roomInfo.groupRights and 8 != 0) && userInfo.roomRole != 2
        report_user_btn.isVisible = report_user_btn_pub.isGone
        report_user_btn.setOnClickListener {
            /** 举报 **/
            activity?.showReportDialog(userInfo.userId, roomId) {
                if (it == 1) {
                    dismissAllowingStateLoss()
                }
            }

        }
        report_user_btn_pub.setOnClickListener {
            //拉黑
            activity?.shieldingPop(this, it, roomInfo, userInfo, back)
        }


        user_avatar_img.glideDisplay(userInfo.avatar, userInfo.gender.genderAvatar(userInfo.isPublisher))
        user_name_tv.text = userInfo.nickname
        user_name_tv.paint.isFakeBoldText = true

        if (userInfo.gender == 1) {
            user_extra_tv.text = "男 ${userInfo.birthYear.orEmpty()}"
            user_extra_tv.setBackColor(Color.parseColor("#6995ff"))
        } else {
            user_extra_tv.text = "女 ${userInfo.birthYear.orEmpty()}"
            user_extra_tv.setBackColor(Color.parseColor("#FF69B0"))
        }

        val interestStr = userInfo.interests.orEmpty().joinToString(",") { it.name }
        user_info_desc_tv.text = "关注的问题:$interestStr"
        user_info_desc_tv.isVisible = userInfo.interests.orEmpty().isNotEmpty()
        tv_flog_pub.isVisible = userInfo.isPublisher == true
        at_tv.setOnClickListener { at() }

        invite_divider.isVisible = roomInfo.isCreator && !roomInfo.isSpecialStudio
        invite_upstream.isVisible = roomInfo.isCreator && !roomInfo.isSpecialStudio

        invite_upstream.paint.isFakeBoldText = true
        invite_upstream.text = userInfo.applyOptionDtoItem?.title ?: "邀请连线"
        invite_upstream.setOnClickNotDoubleListener {
            (activity as? VideoLiveRoomActivity)?.let {
                val applyOptionDtoItem = userInfo.applyOptionDtoItem
                it.inviteUpStream(it.roomId, userInfo.userId, RoomApplyType.parseValue(applyOptionDtoItem?.applyType.orEmpty()), RoomType.parseValue(applyOptionDtoItem?.roomType), applyOptionDtoItem?.costTips.orEmpty())
            }
        }
        level_text.apply {
            isVisible = userInfo.userLevel > 0
            level_text.setPadding(0, 0, if (userInfo.userLevel > 9) dip(5) else dip(10), 0)
            level_text.text = userInfo.userLevel.toString()
            level_text.setBackgroundResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Normal))
        }

        couponList.clear()
        couponList.addAll(userInfo.coupons.orEmpty())
        couponAdapter.notifyDataSetChanged()

        card_coupon_list_indicator.isVisible = couponList.size > 1
        card_coupon_list_indicator.buildIndicator(couponList.size, R.drawable.selector_user_card_coupon_indicators, dip(7.5f))
        card_coupon_list_fl.isVisible = couponList.isNotEmpty()
        if (couponList.size == 1) {
            card_coupon_list.updateLayoutParams<FrameLayout.LayoutParams> {
                gravity = Gravity.CENTER
            }
        }
        card_coupon_list.apply {
            adapter = couponAdapter
            GravitySnapHelper(Gravity.START).apply { snapToPadding = true }.attachToRecyclerView(card_coupon_list)
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    val layoutManager = recyclerView.layoutManager
                    if (layoutManager is LinearLayoutManager) {
                        val curPosition = layoutManager.findFirstCompletelyVisibleItemPosition()
                        card_coupon_list_indicator.selectIndex(curPosition)
                    }
                }
            })
        }
    }

    //@
    private fun at() {
        back(0)
        dismissAllowingStateLoss()
    }


}