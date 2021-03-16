package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.unit.TimeUnit
import com.mei.wood.R
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList.BannerItemBean
import kotlinx.android.synthetic.main.view_intimate_banner_item.view.*

/**
 * Created by hang on 2020/7/10.
 */
class IntimateBannerItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var bannerItemBean: BannerItemBean? = null

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_banner_item)

        setOnClickListener {
            if (context.checkLogin()) {
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", "瀑布流banner",
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", bannerItemBean?.action,
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                Nav.toAmanLink(context, bannerItemBean?.action.orEmpty())
            }
        }


    }

    fun convertData(data: BannerItemBean?, type: RoomItemType) {
        banner_layout.updateLayoutParams {
            height = if (type == RoomItemType.BANNER) dip(215) else ViewGroup.LayoutParams.MATCH_PARENT
        }
        bannerItemBean = data
        banner_image.glideDisplay(data?.image, R.color.color_f0f0f0)
    }

}