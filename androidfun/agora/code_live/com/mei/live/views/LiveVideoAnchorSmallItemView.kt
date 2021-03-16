package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.joker.im.custom.CustomType
import com.mei.base.common.AUDIO_MODE_CHANGE
import com.mei.base.common.WINDOW_MODE_CHANGE
import com.mei.base.network.holder.SpiceHolder
import com.mei.base.weight.textview.formatText
import com.mei.live.manager.genderAvatar
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeVideo
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.extensions.executeToastKt
import com.net.model.room.RoomInfo
import com.net.network.chick.room.RoomKickRequest
import kotlinx.android.synthetic.main.view_video_anchor_small_item.view.*

/**
 * Created by hang on 2020/6/16.
 * 知心达人视角 小窗口
 */
class LiveVideoAnchorSmallItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), LiveVideoItemConfig {

    var mUserId: Int = View.NO_ID

    private var mRoomInfo: RoomInfo? = null

    private var mTimeAccount = -1L

    private var mPopupWindow: PopupWindow? = null

    override var enableVideo: Boolean = true
        set(value) {
            field = value

            updateView()
        }

    private fun updateView() {
        zoom_img.isVisible = mRoomInfo?.isCreator == true && enableVideo
        close_video.isVisible = enableVideo
        close_audio.isVisible = enableVideo

        down_mic.updateLayoutParams<MarginLayoutParams> {
            bottomMargin = if (enableVideo) dip(45) else 0
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_video_anchor_small_item)

        initEvent()
    }

    private fun initView() {
        user_container.isVisible = mUserId != mRoomInfo?.createUser?.userId
        down_mic.isVisible = mUserId != mRoomInfo?.createUser?.userId
        close_video.isVisible = mUserId != mRoomInfo?.createUser?.userId && enableVideo

        zoom_img.isInvisible = mRoomInfo?.isCreator != true || !enableVideo

        getCacheUserInfo(mUserId).let {
            user_avatar.glideDisplay(it?.avatar.orEmpty(), it?.gender.genderAvatar(it?.isPublisher))
            user_name.text = it?.nickname.orEmpty().formatText(5)
        }
    }

    fun showDisableVideoHintPop() {
        if (mUserId != JohnUser.getSharedUser().userID && mUserId != mRoomInfo?.createUser?.userId) {
            close_video.showDisableVideoHintPop(dip(191), dip(52), -dip(116), true) {
                mPopupWindow = it
            }
        }
    }

    private fun initEvent() {
        zoom_img.setOnClickNotDoubleListener {
            postAction(WINDOW_MODE_CHANGE, mUserId)
        }
        down_mic.setOnClickNotDoubleListener {
            (context as? SpiceHolder)?.apiSpiceMgr?.executeToastKt(RoomKickRequest(mRoomInfo?.roomId.orEmpty(), mUserId))
            liveSendCustomMsg(mRoomInfo?.roomId.orEmpty(), CustomType.auto_kick_offline, applyData = {
                targetUserId = mUserId
                userIds = arrayListOf(mUserId)
                targetTips = "你已被抱下麦"
            })
        }
        close_video.setOnClickNotDoubleListener {
            if (mRoomInfo?.roomRole == 2) {
                UIToast.toast("超管不允许关闭用户视频")
            } else {
                disableVideoMode(mUserId)
            }
        }
        close_audio.setOnClickNotDoubleListener {
            postAction(AUDIO_MODE_CHANGE, Pair(mUserId, if (close_audio.isSelected) 0 else 1))
        }

        user_container.setOnClickNotDoubleListener {
            (context as? VideoLiveRoomActivity)?.run {
                showUserInfoDialog(roomInfo, mUserId) { type, _ ->
                    if (type == 0) {
                        showInputKey(getCacheUserInfo(mUserId))
                    }
                }
            }
        }
    }


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPopupWindow?.dismiss()
        mPopupWindow = null
    }

    override fun setUserId(userId: Int) {
        mUserId = userId
        initView()
    }

    override fun setRoomInfo(roomInfo: RoomInfo) {
        mRoomInfo = roomInfo
        initView()
    }

    @SuppressLint("SetTextI18n")
    override fun setTimeAccount(timeAccount: Long) {
        mTimeAccount = timeAccount
        upstream_time.text = "连线中 ${timeAccount.formatTimeVideo()}"
    }

    override fun switchAudioState(isEnable: Boolean) {
        close_audio.isSelected = isEnable
    }
}