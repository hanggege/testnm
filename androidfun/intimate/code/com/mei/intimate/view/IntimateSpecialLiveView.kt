package com.mei.intimate.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.base.weight.textview.autoSplitText
import com.mei.base.weight.textview.formatText
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.login.toLogin
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenWidth
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.date.formatTimeToHMS
import com.mei.orc.util.span.CustomImageSpan
import com.mei.wood.R
import com.mei.wood.extensions.appendSpannable
import com.net.MeiUser
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList
import com.net.model.chick.room.RoomList.BroadcastItemBean
import kotlinx.android.synthetic.main.view_intimate_special_live.view.*

/**
 * Created by hang on 2020/7/10.
 */
class IntimateSpecialLiveView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var broadcastItemBean: BroadcastItemBean? = null

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_special_live)

        live_title.paint.isFakeBoldText = true

        getViewById(id).updateLayoutParams {
            width = screenWidth / 2
        }

        live_cover_image.updateLayoutParams {
            width = screenWidth / 2 - dip(15)
            height = width
        }

        setOnClickNotDoubleListener {
            if (JohnUser.getSharedUser().hasLogin()) {
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", "连线直播",
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", "",
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                VideoLiveRoomActivityLauncher.startActivity(context, broadcastItemBean?.roomId.orEmpty(), LiveEnterType.myself_home_normal_enter)
            } else {
                context.toLogin()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun convertData(data: RoomList.BaseItem<*>) {
        (data.content as? BroadcastItemBean)?.let {
            broadcastItemBean = it

            live_cover_image.glideDisplay(it.background, R.color.color_f0f0f0)
            image_photo.glideDisplay(it.image)

            studio_name.text = it.groupInfo?.groupName.orEmpty()
            studio_name.isVisible = !it.groupInfo?.groupName.isNullOrEmpty()

            left_connect_user.glideDisplay(it.createUser?.avatar, it.createUser?.gender.genderAvatar())
            right_connect_user.glideDisplay(it.userInfo?.avatar, it.userInfo?.gender.genderAvatar(it.userInfo?.isPublisher))

            upstream_time.text = "已连时长${it.duration.formatTimeToHMS()}"

            live_title.text = buildSpannedString {
                if (data.type == RoomItemType.SPECIAL_BROADCAST) {
                    append(autoSplitText(live_title, it.title).formatText(14))
                    append("  ")
                    appendSpannable(" ", CustomImageSpan(context, R.drawable.icon_special_service_title))
                } else {
                    append(it.title.orEmpty())
                    /**暂时隐藏这个*/
//                    val textDrawable = TextDrawable(dip(2).toFloat(), dip(9).toFloat(), R.color.color_5E74FF).apply {
//                        text = it.exclusivePriceTips
//                    }
//                    appendSpannable(" ", CustomImageSpan(textDrawable, 0F))
                }
            }

            intimate_name.text = it.createUser?.nickName.orEmpty()

            icon_exclusive_type.setImageResource(if (data.type == RoomItemType.SPECIAL_BROADCAST) R.drawable.icon_special_service_label
            else R.drawable.icon_exclusive_upstream_label)
        }
    }
}