package com.mei.live.ui.adapter.item

import android.graphics.Color
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StrikethroughSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundRelativeLayout
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.CourseInfo
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.createSplitSpannable
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showExclusiveServiceDialog
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.fragment.LiveIMSplitFragment
import com.mei.live.ui.fragment.getCourseDetailUrl
import com.mei.live.ui.fragment.getSpecialServiceDetailUrl
import com.mei.live.ui.fragment.upstreamUserIds
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.string.getInt
import com.mei.widget.gradient.GradientTextView
import com.mei.widget.gradient.applyViewDelegate
import com.mei.widget.holder.setVisibleAndGone
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.buildFirstIndent
import com.mei.wood.util.MeiUtil
import com.net.model.room.RoomType
import com.net.model.user.UserInfo

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/6/2
 */
class LiveIMCourseServiceCardHolder(val fragment: LiveIMSplitFragment, val itemView: View) : BaseViewHolder(itemView) {


    fun refresh(item: CustomMessage) {
        //服务类型
        val data = item.customInfo?.data as? ChickCustomData
        if (data != null) {
            //殷浩说1000以内的消息取userid
            val userId = if (item.sender.getInt() <= 1000) data.userId else item.sender.getInt(0)
            val userInfo = getCacheUserInfo(userId) ?: getCacheUserInfo(data.userId)

            getView<RoundImageView>(R.id.user_avatar_img).glideDisplay(userInfo?.avatar.orEmpty(), userInfo?.gender.genderAvatar(userInfo?.isPublisher))
            if (data.type == CustomType.special_service_card) {
                serviceExclusiveHolderView(data, userInfo)
            } else if (data.type == CustomType.course_card) {
                courseHolderView(data, userInfo)
            }
        }
    }


    /**
     * 私有服务holder
     */
    private fun serviceExclusiveHolderView(data: ChickCustomData, userInfo: UserInfo?) {
        loadUserInfo(fragment,userInfo, data)

        val serviceAdInfo = data.serviceInfo
        serviceAdInfo?.let { info ->
            setText(R.id.live_msg_content_title, info.serviceName.decodeUrl())
            getView<RoundRelativeLayout>(R.id.live_msg_content_frame).apply {
                setOnClickListener {
                    fragment.activity?.let {
                        val loadUrl = it.getSpecialServiceDetailUrl(info.specialServiceId, fragment.roomInfo)
                        it.showExclusiveServiceDialog(loadUrl, fragment.roomInfo.roomId)
                    }
                }
            }
            setText(R.id.tv_count_all, "${info.serviceCost}")
            var msg = ""
            info.serviceMin.let {
                if (it > 1) {
                    msg = "   ${it}次起"
                }
            }
            setText(R.id.tv_connt_service, "/${info.serviceMinutes}分钟${msg}")
            setVisibleAndGone(R.id.history_service_ll, info.priceText?.isNotEmpty() != true)
            setVisibleAndGone(R.id.price_text_tv, info.priceText?.isNotEmpty() == true)
            setText(R.id.price_text_tv, info.priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))
        }

        getView<ImageView>(R.id.user_avatar_img).apply {
            setOnClickListener {
                fragment.activity.showUserInfoDialog(fragment.roomInfo, userInfo?.userId
                        ?: 0) { type, _ ->
                    if (type == 0) {
                        (fragment.activity as? VideoLiveRoomActivity)?.showInputKey(userInfo)
                    }
                }
            }
        }

    }

    private fun courseHolderView(data: ChickCustomData, userInfo: UserInfo?) {
        loadUserInfo(fragment,userInfo, data)
        val courseInfo = data.courseInfo

        setText(R.id.live_msg_content_title, courseInfo?.courseName.orEmpty().decodeUrl())
        getView<RoundRelativeLayout>(R.id.live_msg_content_frame).apply {
            setOnClickListener {
                fragment.activity?.let {
                    var loadUrl = it.getCourseDetailUrl(courseInfo?.courseId
                            ?: 0, fragment.roomInfo)
                    if (courseInfo?.cardType == CourseInfo.CardType.TYPE_RECOMMEND) {
                        loadUrl = MeiUtil.appendParamsUrl(loadUrl, "is_recommend" to "1")
                    }
                    it.showExclusiveServiceDialog(loadUrl, fragment.roomInfo.roomId)
                }
            }
        }
        setText(R.id.tv_count_all, courseInfo?.cost.toString())
        val hint = String.format(Cxt.getStr(R.string.course_service_text, courseInfo?.audioCount))
        //原价
        val originalPrice = if (courseInfo?.crossedPrice != 0) "${courseInfo?.crossedPrice}" else ""
        if (originalPrice.isNotEmpty()) {
            val ss = SpannableString("$originalPrice $hint")
            ss.setSpan(StrikethroughSpan(), 0, originalPrice.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            setText(R.id.tv_connt_service, ss)
        } else {
            setText(R.id.tv_connt_service, hint)
        }


        setVisibleAndGone(R.id.history_service_ll, courseInfo?.priceText?.isNotEmpty() != true)
        setVisibleAndGone(R.id.price_text_tv, courseInfo?.priceText?.isNotEmpty() == true)
        setText(R.id.price_text_tv, courseInfo?.priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))

        //课程游览埋点
        GrowingUtil.track("function_view",
                "function_name", "课程", "function_page", "直播间",
                "status", if (courseInfo?.hasBuy != true) "未购买" else "已购买")
        getView<ImageView>(R.id.user_avatar_img).apply {
            setOnClickListener {
                fragment.activity.showUserInfoDialog(fragment.roomInfo, userInfo?.userId
                        ?: 0) { type, _ ->
                    if (type == 0) {
                        (fragment.activity as? VideoLiveRoomActivity)?.showInputKey(userInfo)
                    }
                }
            }
        }


    }

}

fun BaseViewHolder.loadUserInfo(fragment: LiveIMSplitFragment,userInfo: UserInfo?, data: ChickCustomData) {
    val isLiveCreator = fragment.roomInfo.createUser?.userId == userInfo?.userId
    val roleTa = getView<TextView>(R.id.live_user_role_ta).apply {
        isVisible = userInfo?.roomRole == 3
    }
    val exclusiveLivingUser = fragment.activity.upstreamUserIds()
            .filterNot { fragment.roomInfo.createUser?.userId == it }
            .any { it == userInfo?.userId }
            && fragment.roomInfo.roomType == RoomType.EXCLUSIVE
            && fragment.roomInfo.isCreator
    val serviceRepresentationView = getView<TextView>(R.id.live_room_message_service).apply {
        val model = if (fragment.roomInfo.isStudio) {
            userInfo?.medalMap.orEmpty()[fragment.roomInfo.groupInfo?.groupId.toString()]
        } else {
            userInfo?.medalMap.orEmpty()[fragment.roomInfo.publisherId.toString()]
        }
        isVisible = model != null
        text = model?.medal.orEmpty()
    }
    val levelView = getView<TextView>(R.id.level_text).apply {
        isVisible = !isLiveCreator && userInfo != null && userInfo.userLevel > 0
        if (userInfo != null) {
            setPadding(0, 0, if (userInfo.userLevel > 9) dip(5) else dip(9), 0)
            text = userInfo.userLevel.toString()
            setBackgroundResource(getBackGroundLevelResource(userInfo.userLevel, LevelSize.Normal))
        }
    }
    val mentorView = getView<ImageView>(R.id.live_mentor_tag_img).apply {
        isVisible = !isLiveCreator && userInfo?.isPublisher == true && userInfo.groupId == 0
    }
    val roleView = getView<GradientTextView>(R.id.live_user_role_view).apply {
        isVisible = isLiveCreator || exclusiveLivingUser
        text = if (isLiveCreator) {
            if (fragment.roomInfo.isStudio) "主讲人" else if (fragment.roomInfo.isSpecialStudio) "咨询师" else "知心达人"
        } else "连麦中"
        isSelected = isLiveCreator
        delegate.applyViewDelegate {
            if (isSelected) {
                startColor = ContextCompat.getColor(context, R.color.color_FF9E40)
                endColor = ContextCompat.getColor(context, R.color.color_FE6B10)
            } else {
                startColor = ContextCompat.getColor(context, R.color.color_74DD49)
                endColor = ContextCompat.getColor(context, R.color.color_5BBE57)
            }
            radius = 8.dp
        }
    }
    val iosUserIcon = getView<ImageView>(R.id.live_ios_user_icon)
    iosUserIcon.isVisible = fragment.roomInfo.roomRole != 0 && userInfo?.clientType == "IOS"

    val studioMemberIcon = getView<ImageView>(R.id.icon_studio_member)
    studioMemberIcon.isVisible = userInfo?.groupId ?: 0 > 0

    val contentTextView = getView<TextView>(R.id.tv_live_name_pub_name)
    contentTextView.text = buildSpannedString {
        data.content.forEach {
            val click: ((String) -> Unit)? = if (it.action.isNotEmpty()) {
                { _ ->
                    Nav.toAmanLink(contentTextView.context, it.action)
                }
            } else null
            appendLink(it.text.decodeUrl(), it.color.parseColor(Color.parseColor("#333333")), click = click)
        }
        var firstIndent = 0
        if (roleTa.isVisible) firstIndent += fragment.dip(40)
        if (roleView.isVisible) firstIndent += fragment.dip(40)
        if (levelView.isVisible) firstIndent += fragment.dip(38)
        if (mentorView.isVisible) firstIndent += fragment.dip(20)
        if (serviceRepresentationView.isVisible) firstIndent += fragment.dip(55)
        if (iosUserIcon.isVisible) firstIndent += fragment.dip(19)
        if (studioMemberIcon.isVisible) firstIndent += fragment.dip(19)
        buildFirstIndent(firstIndent)
    }
}