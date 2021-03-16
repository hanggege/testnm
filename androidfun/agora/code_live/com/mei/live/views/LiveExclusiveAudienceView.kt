package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.doOnPreDraw
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import com.google.android.material.shape.*
import com.mei.base.common.ROOM_TYPE_CHANGE
import com.mei.base.network.holder.SpiceHolder
import com.mei.im.ui.dialog.showIMServiceWebBottomFragmentDialog
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.live.ui.fragment.EXCLUSIVE_SHOW_UPSTREAM_HINT
import com.mei.live.ui.fragment.SPECIAL_SHOW_UPSTREAM_HINT
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.dp
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeVideo
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.net.MeiUser
import com.net.model.room.RoomInfo
import com.net.model.room.RoomType
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import kotlinx.android.synthetic.main.view_exclusive_audience.view.*

/**
 * Created by hang on 2020/6/17.
 * 观众端私密连线界面
 */
class LiveExclusiveAudienceView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    var mTimeAccount = -1L
        @SuppressLint("SetTextI18n")
        set(value) {
            field = value
            upstream_time.text = "已连线${value.formatTimeVideo()}"
        }

    var roomInfo = RoomInfo()


    private var currMentorId = View.NO_ID
    private var currUserId = View.NO_ID

    var upstreamProtectHintPop: PopupWindow? = null

    private val apiClient by lazy { (context as? SpiceHolder)?.apiSpiceMgr }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_exclusive_audience)

        mentor_name.paint.isFakeBoldText = true
        user_name.paint.isFakeBoldText = true
        upstream_type.paint.isFakeBoldText = true

        icon_exclusive_lock.setOnClickNotDoubleListener {
            if (upstreamProtectHintPop?.isShowing != true) {
                upstreamProtectHintPop = icon_exclusive_lock.showUpstreamProtectHintPop(if (roomInfo.isSpecialService) 2 else 0)
            }
        }

        studio_live_layout.setOnClickNotDoubleListener {
            VideoLiveRoomActivityLauncher.startActivity(context, roomInfo.groupInfo?.roomId.orEmpty(), LiveEnterType.special_room_page)
        }

        consult_hint.setOnClickNotDoubleListener {
            val url = MeiUtil.appendParamsUrl(AmanLink.NetUrl.instructions,
                    "index" to "4",
                    "group_id" to (MeiUser.getSharedUser().info?.groupInfo?.groupId
                            ?: 0).toString(),
                    "from" to "im")
            (context as? FragmentActivity)?.showIMServiceWebBottomFragmentDialog(url)
        }

        mentor_avatar.setOnClickNotDoubleListener {
            if (currMentorId > 0) {
                (context as? VideoLiveRoomActivity)?.run {
                    showUserInfoDialog(roomInfo, currMentorId) { type, _ ->
                        if (type == 0) {
                            showInputKey(getCacheUserInfo(currMentorId))
                        }
                    }
                }
            }
        }

        user_avatar.setOnClickNotDoubleListener {
            if (currUserId > 0) {
                (context as? VideoLiveRoomActivity)?.run {
                    showUserInfoDialog(roomInfo, currUserId) { type, _ ->
                        if (type == 0) {
                            showInputKey(getCacheUserInfo(currUserId))
                        }
                    }
                }
            }
        }

        (context as? LifecycleOwner)?.bindAction<Set<Int>>(ROOM_TYPE_CHANGE) {
            it?.also {
                upstreamProtectHintPop?.dismiss()
                refreshInfo(it)
            }
        }
    }

    fun refreshInfo(userIds: Set<Int>) {
        userIds.forEach {
            if (it == roomInfo.publisherId) {
                currMentorId = it
            } else {
                currUserId = it
            }
        }
        if (roomInfo.isSpecialStudio && userIds.size == 1 && userIds.contains(currMentorId)) {
            currUserId = 0
        }
        val id = JohnUser.getSharedUser().userID
        if (roomInfo.roomRole == 2 || roomInfo.groupRights and 8 != 0 || roomInfo.roomType == RoomType.COMMON || id == currMentorId || id == currUserId) {
            isVisible = false
            upstream_gif.stopAnimation(true)
            return
        }
        if (!isVisible) {
            val type = if (roomInfo.isSpecialService) 2 else 0
            icon_exclusive_lock.doOnPreDraw {
                if (upstreamProtectHintPop?.isShowing != true) {
                    upstreamProtectHintPop = icon_exclusive_lock.autoShowHintPop(type)
                }
            }

        }
        isVisible = true

        studio_cover.glideDisplay(roomInfo.groupInfo?.avatar.orEmpty(), R.drawable.default_studio_cover)
        studio_name.text = roomInfo.groupInfo?.groupName.orEmpty()

        upstream_type_layout.setBackgroundResource(if (roomInfo.specialServiceSampleDto == null && !roomInfo.isSpecialStudio) R.drawable.icon_exclusive_audience_top else R.drawable.icon_special_top)
        upstream_type.setCompoundDrawablesWithIntrinsicBounds(if (roomInfo.specialServiceSampleDto == null && !roomInfo.isSpecialStudio) R.drawable.icon_exclusive_protect_audience_big else R.drawable.icon_special_protect_audience_small, 0, 0, 0)
        upstream_type.text = if (roomInfo.specialServiceSampleDto == null && !roomInfo.isSpecialStudio) "私密连线中" else "专属服务中"
        consult_hint.text = if (roomInfo.specialServiceSampleDto == null && !roomInfo.isSpecialStudio) "如何私密连线" else "如何专属服务"
        if (currMentorId > View.NO_ID) {
            apiClient?.requestUserInfo(arrayOf(currMentorId)) {
                it.firstOrNull().let { info ->
                    mentor_avatar.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(true))
                    mentor_name.text = info?.nickname.orEmpty()
                }
            }
        }
        if (currUserId > View.NO_ID) {
            apiClient?.requestUserInfo(arrayOf(currUserId)) {
                it.firstOrNull().let { info ->
                    user_avatar.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(false))
                    user_name.text = info?.nickname.orEmpty()
                }
            }
        }
        if (currMentorId > 0 && currUserId > 0) {
            SVGAParser.shareParser().decodeFromAssets("gif_broadcast.svga", object : SVGAParser.ParseCompletion {
                override fun onComplete(videoItem: SVGAVideoEntity) {
                    upstream_gif.setVideoItem(videoItem)
                    upstream_gif.startAnimation()
                }

                override fun onError() {

                }

            })
        }
    }
}

/**
 * 连麦成功自动显示pop
 */
fun View.autoShowHintPop(type: Int): PopupWindow? {
    val isShow = if (type == 0 || type == 1) {
        EXCLUSIVE_SHOW_UPSTREAM_HINT.getBooleanMMKV(true)
    } else {
        SPECIAL_SHOW_UPSTREAM_HINT.getBooleanMMKV(true)
    }
    return if (isShow) {
        showUpstreamProtectHintPop(type)
    } else {
        null
    }
}

/**
 * 提示用户连线内容保密弹窗
 * @param type 0-》私密观众 1-》私密上麦端 2-》专属观众 3-》专属上麦端
 */
fun View.showUpstreamProtectHintPop(type: Int): PopupWindow {
    if (type == 0 || type == 1) {
        EXCLUSIVE_SHOW_UPSTREAM_HINT.putMMKV(true)
    } else {
        SPECIAL_SHOW_UPSTREAM_HINT.putMMKV(true)
    }

    val popUpView = layoutInflaterKt(R.layout.pop_service_hint)

    val popupWindow = CommonPopupWindow.Builder(context)
            .setView(popUpView)
            .setWidthAndHeight(dip(190), dip(100.5f))
            .setAnimationStyle(R.style.AnimDown)
            .setOutsideTouchable(false)
            .create()

    val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(CornerFamily.ROUNDED, 4.dp)
            .setTopEdge(object : TriangleEdgeTreatment(5.dp, false) {
                override fun getEdgePath(length: Float, center: Float, interpolation: Float, shapePath: ShapePath) {
                    super.getEdgePath(length, length * 0.6f, interpolation, shapePath)
                }
            })
            .build()
    val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
        paintStyle = Paint.Style.FILL
        val colorResId = when (type) {
            0 -> Color.parseColor("#9938AAFE")
            1 -> Color.parseColor("#AA38AAFE")
            2 -> Color.parseColor("#807381FF")
            else -> Color.parseColor("#AA7381FF")
        }
        setTint(colorResId)
    }
    popUpView.findViewById<View>(R.id.pop_service_hint_layout).apply {
        (parent as? ViewGroup)?.clipChildren = false
        background = backgroundDrawable
    }
    popUpView.setOnClickListener {
        popupWindow?.dismiss()
        if (type == 0 || type == 1) {
            EXCLUSIVE_SHOW_UPSTREAM_HINT.putMMKV(false)
        } else {
            SPECIAL_SHOW_UPSTREAM_HINT.putMMKV(false)
        }
    }

    if (this is ViewGroup) { // 区分是否是专属服务弹窗,对应位置给出不同间距
        popupWindow.showAsDropDown(this, -dip(10), 0, Gravity.END)
    } else {
        popupWindow.showAsDropDown(this, dip(35), 0, Gravity.END)
    }
    return popupWindow
}