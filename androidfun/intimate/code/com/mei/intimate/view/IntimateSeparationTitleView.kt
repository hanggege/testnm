package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.login.checkLogin
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.net.model.chick.room.RoomList.SeparationTitle
import kotlinx.android.synthetic.main.view_intimate_separation_title.view.*

/**
 * IntimateNewTitleView
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-02
 */
class IntimateSeparationTitleView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {

    private var data = SeparationTitle()

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_separation_title)

        intimate_separation_main_title.paint.isFakeBoldText = true

        intimate_separation_title_more_click.setOnClickNotDoubleListener(tag = "intimate_title_more") {
            if (context.checkLogin()) {
                GrowingUtil.track("main_app_gn_use_data",
                        "screen_name", "首页",
                        "main_app_gn_type", data.title,
                        "time_stamp", (System.currentTimeMillis() / TimeUnit.SECOND).toString(),
                        "main_app_gn_label", "更多",
                        "position", "",
                        "main_app_gn_mentor_id", "",
                        "main_app_gn_content", "",
                        "main_app_gn_id", "",
                        "main_app_gn_cate", "",
                        "main_app_gn_gender", "",
                        "main_app_gn_pro", "")
                Nav.toAmanLink(context, data.moreAction)
            }
        }
    }

    fun convertData(titleData: SeparationTitle?) {
        titleData?.let {
            data = it
            intimate_separation_main_title.text = data.title
            intimate_separation_sub_title.isGone = data.subTitle.isNullOrEmpty()
            data.subTitle?.let { title -> intimate_separation_sub_title.text = title }
            intimate_separation_title_more.isGone = data.moreTitle.isNullOrEmpty()
            data.moreTitle?.let { title -> intimate_separation_title_more_text.text = title }
        }
    }

}