package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.login.checkLogin
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.net.model.chick.room.RoomList.OnlinePublisher
import kotlinx.android.synthetic.main.view_intimate_consult_online.view.*

/**
 * IntimateConsultOnlineView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-02
 */
class IntimateConsultOnlineView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var data = OnlinePublisher()

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_consult_online)

        setOnClickNotDoubleListener(tag = "intimate_online_publisher") {
            if (context.checkLogin()) {
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", "知心达人在线",
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", "",
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                Nav.toAmanLink(context, data.action)
            }
        }
        intimate_consult_online_name.paint.isFakeBoldText = true
    }

    fun convertData(onlinePublisher: OnlinePublisher?) {
        onlinePublisher?.apply {
            data = onlinePublisher
            intimate_consult_online_avatar_layout.delegate.backgroundColor = data.background.parseColor(ContextCompat.getColor(context, android.R.color.white))
            intimate_consult_online_avatar.glideDisplay(data.image.orEmpty())
            intimate_consult_online_name.text = data.name
            if (data.tagList.isNullOrEmpty()) {
                intimate_consult_online_label_0.isInvisible = true
                intimate_consult_online_label_1.isInvisible = true
                return
            }
            when (data.tagList.size) {
                1 -> {
                    intimate_consult_online_label_0.isInvisible = false
                    intimate_consult_online_label_1.isInvisible = true
                    intimate_consult_online_label_0.text = data.tagList[0]
                }
                else -> {
                    intimate_consult_online_label_0.isInvisible = false
                    intimate_consult_online_label_1.isInvisible = false
                    intimate_consult_online_label_0.text = data.tagList[1]
                    intimate_consult_online_label_1.text = data.tagList[0]
                }
            }
        }
    }

}