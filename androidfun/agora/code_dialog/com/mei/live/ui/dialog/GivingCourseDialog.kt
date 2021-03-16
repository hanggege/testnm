package com.mei.live.ui.dialog

import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.ChickCustomData
import com.mei.base.ui.nav.Nav
import com.mei.base.util.shadow.setListShadowDefault
import com.mei.base.weight.relative.RoundConstraintLayout
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.Cxt
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher

/**
 *
 * @author Created by lenna on 2020/11/9
 * 赠送课程弹框
 */
fun FragmentActivity.showGivingCourseDialog(data: ChickCustomData?) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_giving_course)
    /**关闭弹窗*/
    contentView.findViewById<ImageView>(R.id.close_giving_course_dialog)
            .setOnClickListener {
                dialog.dismiss()
            }
    contentView.findViewById<RoundConstraintLayout>(R.id.giving_course_content_card_bg).apply {
        setListShadowDefault(this)
    }
    /**顶部背景*/
    contentView.findViewById<ImageView>(R.id.giving_course_top_bg).apply {
        glideDisplay(data?.cardPopup?.bgImg.orEmpty())
    }
    /**用户头像*/
    contentView.findViewById<RoundImageView>(R.id.giving_course_user_avatar_riv).apply {
        glideDisplay(data?.cardPopup?.avatar.orEmpty(), R.drawable.default_avatar_72)
    }
    /**标题*/
    contentView.findViewById<TextView>(R.id.giving_course_title_tv).apply {
        text = data?.cardPopup?.title.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333))
    }
    /**课程标题*/
    contentView.findViewById<TextView>(R.id.giving_course_card_content_tv).apply {
        isVisible = data?.card?.title?.isNotEmpty() == true
        paint.isFakeBoldText = true
        text = data?.card?.title.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333))
    }
    /**课程价格*/
    contentView.findViewById<TextView>(R.id.giving_course_content_hint_tv).apply {
        isVisible = data?.card?.subTitle?.isNotEmpty() == true
        text = data?.card?.subTitle.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_999999))
    }
    /**按钮操作*/
    contentView.findViewById<TextView>(R.id.giving_course_btn).apply {
        isVisible = data?.cardPopup?.buttons?.isNotEmpty() == true
        text = data?.cardPopup?.buttons.orEmpty().createSplitSpannable(Cxt.getColor(android.R.color.white))
        setOnClickListener {
            Nav.toAmanLink(context, data?.cardPopup?.buttons?.get(0)?.action.orEmpty())
            dialog.dismiss()
        }
    }
    dialog.showDialog(this, DialogData(customView = contentView
            , canceledOnTouchOutside = false
            , isCustomViewGravityTop = true
            , backCanceled = false
            , key = "GivingCourseDialog"
            , ignoreBackground = true), back = {})
}