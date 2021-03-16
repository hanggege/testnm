package com.mei.intimate.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.login.toLogin
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.net.MeiUser
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList
import com.net.model.chick.room.RoomList.BroadcastItemBean
import kotlinx.android.synthetic.main.view_intimate_common_live.view.*

/**
 * Created by hang on 2020/7/10.
 */
class IntimateCommonLiveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var broadcastItemBean: BroadcastItemBean? = null

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_common_live)

        live_title.paint.isFakeBoldText = true
        live_cate_name.paint.isFakeBoldText = true

        getViewById(id).updateLayoutParams {
            width = screenWidth / 2
        }

        live_cover_image.updateLayoutParams {
            width = screenWidth / 2 - dip(15)
            height = width
        }

        setOnClickNotDoubleListener {
            GrowingUtil.track("main_app_gn_use_data",
                    "screen_name", "首页",
                    "main_app_gn_type", "公开直播",
                    "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                    "main_app_gn_label", "",
                    "position", "",
                    "main_app_gn_mentor_id", "",
                    "main_app_gn_content", "",
                    "main_app_gn_id", "",
                    "main_app_gn_cate", "",
                    "main_app_gn_gender", "",
                    "main_app_gn_pro", "")
            if (JohnUser.getSharedUser().hasLogin()) {
                VideoLiveRoomActivityLauncher.startActivity(context, broadcastItemBean?.roomId.orEmpty(), LiveEnterType.myself_home_normal_enter,null,"",broadcastItemBean?.tag.orEmpty())
            } else {
                context.toLogin()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun convertData(data: RoomList.BaseItem<*>) {
        (data.content as? BroadcastItemBean)?.let {
            broadcastItemBean = it
            live_cate_name.text = it.tag.orEmpty()
            live_cate_living.glideDisplay(R.drawable.gif_intimate_living)
            live_cover_image.glideDisplay(it.background, R.color.color_f0f0f0)
            image_photo.glideDisplay(it.image)

            watch_number.text = "${it.onLineCount}人气"
            live_state.isVisible = it.onLineCount > 0
            if (it.onLineCount > 0 && animator?.isRunning == false) {
                startAlphaAnim(live_point)
            }

            studio_name.text = it.groupInfo?.groupName.orEmpty()
            studio_name.isVisible = !it.groupInfo?.groupName.isNullOrEmpty()

            live_title.text = it.title.orEmpty()
            intimate_name.text = it.createUser?.nickName.orEmpty()
        }
        live_gif.isVisible = data.type == RoomItemType.SCHEDULE_BROADCAST
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }

    private var animator: ObjectAnimator? = null
    private fun startAlphaAnim(view: View) {
        if (animator == null) {
            animator = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f)
        }
        animator?.duration = 600
        animator?.repeatMode = ValueAnimator.REVERSE
        animator?.repeatCount = ValueAnimator.INFINITE
        animator?.start()
    }
}