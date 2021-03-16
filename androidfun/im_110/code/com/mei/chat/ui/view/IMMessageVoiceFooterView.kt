package com.mei.chat.ui.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.mei.live.manager.genderAvatar
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.MeiUser
import kotlinx.android.synthetic.main.include_avatar_42.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/18
 */
class IMMessageVoiceFooterView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : FrameLayout(context, attrs, defStyleAttr) {
    init {
        layoutInflaterKtToParentAttach(R.layout.im_row_send_voice_recording_item)
        isVisible = false
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        avatar_img.glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
//        vip_sign_img.isVisible = MeiUser.getSharedUser().isVip
    }


}