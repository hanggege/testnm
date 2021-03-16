package com.mei.live.ui.dialog

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.HonorMedal
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.live.ui.dialog.adapter.MedalAdapter
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.dialog.DialogBack
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.report.HonorDialogReportRequest

/**
 *
 * @author Created by lenna on 2020/10/12
 */

/**
 * 单个勋章，主播奖杯弹框
 */
fun FragmentActivity.showMedalDialog(data: ChickCustomData?, back: (Int) -> Unit) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_medal)

    contentView.findViewById<TextView>(R.id.medal_name_tv)?.apply {
        text = data?.medalInfo?.medals?.firstOrNull()?.title.orEmpty()
        paint.isFakeBoldText = true
    }

    contentView.findViewById<TextView>(R.id.medal_hint_tv)?.text = data?.medalInfo?.medals?.firstOrNull()?.intro.orEmpty()
    contentView.findViewById<TextView>(R.id.medal_btn_content_tv)?.text = data?.medalInfo?.btnText.orEmpty()
    contentView.findViewById<View>(R.id.medal_btn_gfl)?.setOnClickListener {
        dialog.dismiss()
        Nav.toAmanLink(dialog.context, data?.medalInfo?.action.orEmpty())
        if (data?.medalInfo?.action?.isNotEmpty() == true) {
            statShowHonorMedalClick(data.medalInfo?.btnText.orEmpty())
        }
    }
    //勋章
    contentView.findViewById<ImageView>(R.id.medal_iv)?.glideDisplay(data?.medalInfo?.ribbon.orEmpty())
    contentView.findViewById<ImageView>(R.id.medal_center_iv)?.glideDisplay(data?.medalInfo?.medals?.firstOrNull()?.shellImg.orEmpty())
    contentView.findViewById<ImageView>(R.id.medal_hint_title_iv)?.glideDisplay(data?.medalInfo?.titleBgImg.orEmpty())
    contentView.findViewById<ImageView>(R.id.medal_inner_center_iv)?.glideDisplay(data?.medalInfo?.medals?.firstOrNull()?.innerImg.orEmpty())
    contentView.findViewById<TextView>(R.id.medal_hint_title_tv)?.apply {
        text = data?.medalInfo?.title.orEmpty()
        isVisible = data?.medalInfo?.titleBgImg?.isNotEmpty() == true
    }


    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = true, backCanceled = true, key = "MedalDialogFragment", isCustomViewGravityTop = true, ignoreBackground = true), back = object : DialogBack {
        override fun handle(state: Int) {
            if (!contentView.isSelected) back(state)
        }
    })
    if (data?.medalInfo?.action?.isNotEmpty() == true) {
        statShowHonorMedalBrowse()
    }
    (this as? MeiCustomBarActivity)?.apiSpiceMgr?.executeKt(HonorDialogReportRequest(data?.msgId.orEmpty()))
}

/**
 * 多个勋章弹框
 */
fun FragmentActivity.showMoreMedalDialog(data: ChickCustomData?, back: (Int) -> Unit) {
    val list: MutableList<HonorMedal> = arrayListOf()
    val medalAdapter by lazy {
        MedalAdapter(list)
    }

    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_more_medal)

    contentView.findViewById<View>(R.id.more_medal_btn_gfl)?.setOnClickListener {
        dialog.dismiss()
        Nav.toAmanLink(dialog.context, data?.medalInfo?.action.orEmpty())
        if (data?.medalInfo?.action?.isNotEmpty() == true) {
            statShowHonorMedalClick(data.medalInfo?.btnText.orEmpty())
        }
    }
    contentView.findViewById<TextView>(R.id.more_medal_btn_tv)?.text = data?.medalInfo?.btnText.orEmpty()
    contentView.findViewById<TextView>(R.id.more_medal_title_tv)?.text = data?.medalInfo?.title.orEmpty()
    list.clear()
    list.addAll(data?.medalInfo?.medals ?: arrayListOf())
    contentView.findViewById<RecyclerView>(R.id.more_medal_rv)?.apply {
        adapter = medalAdapter
        layoutManager = GridLayoutManager(dialog.context, if (list.size > 2) 3 else 2)
        updateLayoutParams {
            height = if (list.size > 3) dip(188) else ViewGroup.LayoutParams.WRAP_CONTENT
        }
    }
    contentView.findViewById<ImageView>(R.id.more_medal_bg_iv)?.glideDisplay(data?.medalInfo?.background.orEmpty())
    contentView.findViewById<ImageView>(R.id.medal_title_bg_iv)?.glideDisplay(data?.medalInfo?.titleBgImg.orEmpty())

    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = true, backCanceled = true, key = "MoreMedalDialogFragment", isCustomViewGravityTop = true, ignoreBackground = true), back = object : DialogBack {
        override fun handle(state: Int) {
            if (!contentView.isSelected) back(state)
        }
    })
    if (data?.medalInfo?.action?.isNotEmpty() == true) {
        statShowHonorMedalBrowse()
    }
    (this as? MeiCustomBarActivity)?.apiSpiceMgr?.executeKt(HonorDialogReportRequest(data?.msgId.orEmpty()))
}


/**荣誉勋章弹框预览*/
private fun statShowHonorMedalBrowse() = try {
    GrowingUtil.track("push_popup_view",
            "function_name", "",
            "function_page", "",
            "popup_type", "获得勋章弹窗")
} catch (e: Exception) {
    e.printStackTrace()
}

/**荣誉勋章弹框预览*/
private fun statShowHonorMedalClick(btnStr: String) = try {
    GrowingUtil.track("push_popup_click",
            "popup_type", "获得勋章弹窗",
            "function_page", "",
            "popup_click_type", btnStr)
} catch (e: Exception) {
    e.printStackTrace()
}