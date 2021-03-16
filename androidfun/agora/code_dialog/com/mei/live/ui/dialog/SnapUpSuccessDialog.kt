package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import com.mei.GrowingUtil
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.ui.MeiCustomBarActivity

/**
 * SnapUpSuccessDialog
 *
 * 快速咨询-抢券成功弹窗
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-13
 */
@SuppressLint("SetTextI18n")
fun MeiCustomBarActivity.showSnapUpSuccessDialog(time: Int, couponNum: Long) {

    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_snap_up_success)

    contentView.apply {
        findViewById<TextView>(R.id.snap_up_label).paint.isFakeBoldText = true
        findViewById<TextView>(R.id.snap_up_time).text = "${time}分钟"
        findViewById<TextView>(R.id.snap_up_hint_right).apply {
            paint.isFakeBoldText = true
            text = "免费咨询${time}分钟"
        }

        findViewById<ImageView>(R.id.snap_up_close).setOnClickListener {
            dialog.dismissAllowingStateLoss()
        }

        findViewById<TextView>(R.id.snap_up_advisory).apply {
            paint.isFakeBoldText = true
            setOnClickNotDoubleListener(tag = "snap_up_success") {
                GrowingUtil.track("function_click",
                        "function_name", "快速咨询",
                        "function_page", "抢券成功页",
                        "status", "新用户",
                        "click_type", "开心收下")
                /**这里经过改版不需要弹起立即咨询弹框，点击按钮弹框消失*/
//                (activity as? MeiCustomActivity)?.showConsultDirectionDialog(couponNum)
                dialog.dismissAllowingStateLoss()
            }
        }

    }

    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false, backCanceled = false), okBack = null)

    GrowingUtil.track("function_view",
            "function_name", "快速咨询",
            "function_page", "抢券成功页",
            "status", "新用户")
}