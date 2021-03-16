package com.mei.live.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.mei.base.common.AUDIO_MODE_CHANGE
import com.mei.base.weight.textview.formatText
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.orc.event.postAction
import com.mei.orc.ext.*
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeVideo
import com.mei.widget.gradient.applyViewDelegate
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.net.model.room.RoomInfo
import kotlinx.android.synthetic.main.view_video_user_parallel_item.view.*

/**
 * Created by hang on 2020/6/16.
 * 上麦用户视角 平行窗口
 */
class LiveVideoUserParallelItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        FrameLayout(context, attrs, defStyleAttr), LiveVideoItemConfig {

    var mUserId: Int = View.NO_ID

    private var mRoomInfo: RoomInfo? = null

    private var mTimeAccount = -1L

    private var mPopupWindow: PopupWindow? = null

    init {
        layoutInflaterKtToParentAttach(R.layout.view_video_user_parallel_item)

        initEvent()
    }

    private fun initView() {
        user_container.isVisible = mUserId == JohnUser.getSharedUser().userID
        anchor_container.isVisible = mUserId != JohnUser.getSharedUser().userID
        close_video.isVisible = mUserId == JohnUser.getSharedUser().userID
        close_audio.isVisible = mUserId == JohnUser.getSharedUser().userID

        getCacheUserInfo(mUserId).let {
            user_avatar.glideDisplay(it?.avatar.orEmpty(), it?.gender.genderAvatar(it?.isPublisher))
            user_name.text = it?.nickname.orEmpty().formatText(5)

            anchor_name.text = it?.nickname.orEmpty().formatText(5)
            anchor_avatar.glideDisplay(it?.avatar.orEmpty(), it?.gender.genderAvatar(it?.isPublisher))
        }
        logo_intimate_man.apply {
            delegate.applyViewDelegate {
                if (mUserId == mRoomInfo?.createUser?.userId) {
                    startColor = ContextCompat.getColor(context, R.color.color_FE6B10)
                    endColor = ContextCompat.getColor(context, R.color.color_FF9E40)
                    direction = 1
                } else {
                    strokeWidth = 0.3f.dp2px
                    strokeStartColor = ContextCompat.getColor(context, R.color.color_90FFFFFF)
                }
                radius = 8.dp
            }
            text = (mUserId == mRoomInfo?.createUser?.userId).selectBy(if (mRoomInfo?.isSpecialStudio == true) "咨询师" else "知心达人", "连麦中")
            setTextColor((mUserId == mRoomInfo?.createUser?.userId).selectBy(Color.WHITE, Color.parseColor("#90ffffff")))
        }
    }

    private fun initEvent() {
        close_video.setOnClickNotDoubleListener {
            disableVideoMode(mUserId)
        }

        close_audio.setOnClickNotDoubleListener {
            postAction(AUDIO_MODE_CHANGE, Pair(mUserId, if (close_audio.isSelected) 0 else 1))
        }

        user_container.setOnClickListener {
            (context as? VideoLiveRoomActivity)?.run {
                showUserInfoDialog(roomInfo, mUserId) { type, _ ->
                    if (type == 0) {
                        showInputKey(getCacheUserInfo(mUserId))
                    }
                }
            }
        }
        anchor_container.setOnClickListener {
            user_container.performClick()
        }
    }

    fun showDisableVideoHintPop() {
        if (mUserId == JohnUser.getSharedUser().userID) {
            close_video.showDisableVideoHintPop(dip(191), dip(52), -dip(60), false) {
                mPopupWindow = it
            }
        }
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        mPopupWindow?.dismiss()
        mPopupWindow = null
    }

    @SuppressLint("SetTextI18n")
    override fun setTimeAccount(timeAccount: Long) {
        mTimeAccount = timeAccount
        upstream_time.text = "连线中 ${timeAccount.formatTimeVideo()}"
    }

    override fun switchAudioState(isEnable: Boolean) {
        anchor_close_audio.isVisible = isEnable
        close_audio.isSelected = isEnable
    }
}