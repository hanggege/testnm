package com.mei.wood.dialog

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.core.net.toUri
import androidx.fragment.app.FragmentActivity
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.init.spiceHolder
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.util.permission.JumpPermissionManagement
import com.mei.orc.util.save.getLongMMKV
import com.mei.orc.util.save.putMMKV
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.BuildConfig
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.network.chick.update.VersionCheckRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/27
 */

private var app_update_is_check_over = false

@Suppress("IMPLICIT_CAST_TO_ANY")
fun FragmentActivity.checkAppUpgrade() {
    if (!app_update_is_check_over) {
        app_update_is_check_over = true
        spiceHolder().apiSpiceMgr.executeKt(VersionCheckRequest(), success = {
            if (it.isSuccess) {
                it.data?.let { info ->
                    val keySaveDays = "update_prompt_ver_${info.updateVersion}"
                    val betweenDays: Long = System.currentTimeMillis() - keySaveDays.getLongMMKV()
                    when {
                        info.force -> {
                            val tips = if (info.updateMessage.orEmpty().isNotEmpty()) info.updateMessage else "有新版本更新，请立即更新升级，\\n否则将不能正常使用APP"
                            val contentView = layoutInflaterKt(R.layout.app_upgrade_layout)
                            BaseViewHolder(contentView).setVisibleAndGone(R.id.update_cancel, false)
                                    .setVisibleAndGone(R.id.update_center_line, false)
                                    .setText(R.id.update_content, tips)
                            contentView.findViewById<View>(R.id.update_now).setOnClickListener {
                                gotoDownNewApk(info.path)
                            }
                            NormalDialog().showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false, backCanceled = false), okBack = null)
                        }
                        betweenDays.toDay() >= info.delayRemindDay -> {
                            val tips = if (info.updateMessage.orEmpty().isNotEmpty()) info.updateMessage else "有新版本了，赶紧更新试试"
                            val contentView = layoutInflaterKt(R.layout.app_upgrade_layout)
                            BaseViewHolder(contentView).setVisible(R.id.update_cancel, true)
                                    .setVisible(R.id.update_center_line, true)
                                    .setText(R.id.update_content, tips)
                            contentView.findViewById<View>(R.id.update_now).setOnClickListener {
                                gotoDownNewApk(info.path)
                                /**存当前时间**/
                                keySaveDays.putMMKV(System.currentTimeMillis())
                            }
                            val normalDialog = NormalDialog().showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false), okBack = null)
                            contentView.findViewById<View>(R.id.update_cancel).setOnClickListener {
                                normalDialog.dismissAllowingStateLoss()
                                /**存当前时间**/
                                keySaveDays.putMMKV(System.currentTimeMillis())
                            }
                        }
                        else -> {
                        }
                    }
                }
            }
        })
    }
}


private fun Context.gotoDownNewApk(apkPath: String?) {
    if (apkPath.isNullOrEmpty()) {
        val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(goToMarket)
    } else {
        val intent = Intent(Intent.ACTION_VIEW, apkPath.toUri())
        if (JumpPermissionManagement.isIntentAvailable(this, intent)) {
            startActivity(intent)
        }
    }
}

private fun Long.toDay(): Long = this / 1000 / 60 / 60 / 24