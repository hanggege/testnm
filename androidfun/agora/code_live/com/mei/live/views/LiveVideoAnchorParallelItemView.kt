package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
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
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeVideo
import com.mei.orc.util.save.getBooleanMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.extensions.executeToastKt
import com.net.model.room.RoomInfo
import com.net.network.chick.room.RoomKickRequest
import kotlinx.android.synthetic.main.view_video_anchor_parallel_item.view.*

/**
 * Created by hang on 2020/6/16.
 * 知心达人视角 平行窗口
 */
class LiveVideoAnchorParallelItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), LiveVideoItemConfig {

    var mUserId: Int = View.NO_ID

    private var mRoomInfo: RoomInfo? = null

    private var mTimeAccount = -1L

    private var mPopupWindow: PopupWindow? = null


    init {
        layoutInflaterKtToParentAttach(R.layout.view_video_anchor_parallel_item)

        initEvent()
    }

    private fun initView() {
        user_container.isVisible = mUserId != mRoomInfo?.createUser?.userId
        down_mic.isVisible = mUserId != mRoomInfo?.createUser?.userId
        close_video.isVisible = mUserId != mRoomInfo?.createUser?.userId

        zoom_img.isInvisible = mRoomInfo?.isCreator == false

        getCacheUserInfo(mUserId).let {
            user_avatar.glideDisplay(it?.avatar.orEmpty(), it?.gender.genderAvatar(it?.isPublisher))
            user_name.text = it?.nickname.orEmpty().formatText(5)
        }

    }

    fun showDisableVideoHintPop() {
        if (mUserId != JohnUser.getSharedUser().userID && mUserId != mRoomInfo?.createUser?.userId) {
            close_video.showDisableVideoHintPop(dip(191), dip(52), -dip(108), true) {
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

    override var enableVideo: Boolean = true

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

private const val SHOW_DISABLE_VIDEO_POP = "show_disable_video_pop"
fun View.showDisableVideoHintPop(width: Int, height: Int, offset: Int, isCreator: Boolean, back: (PopupWindow) -> Unit) {
    if (!SHOW_DISABLE_VIDEO_POP.getBooleanMMKV(false)) {
        SHOW_DISABLE_VIDEO_POP.putMMKV(true)
        postDelayed({
            if (isAttachedToWindow) {
                val popupWindow = PopupWindow(context).apply {
                    this.width = width
                    this.height = height
                    isFocusable = true
                    isOutsideTouchable = true
                    setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                    contentView = layoutInflaterKt(R.layout.disable_video_pop_layout).apply {
                        findViewById<ImageView>(R.id.pop_view).setImageResource(
                                if (isCreator) R.drawable.bg_anchor_close_video_hint else R.drawable.bg_user_close_video_hint)
                    }
                }
                popupWindow.showAsDropDown(this, offset, -dip(2))
                back(popupWindow)
                postDelayed({
                    if (isAttachedToWindow) {
                        popupWindow.dismiss()
                    }
                }, 5000)
            }
        }, 10000)
    }
}
