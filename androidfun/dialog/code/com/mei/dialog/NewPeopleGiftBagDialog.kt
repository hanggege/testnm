package com.mei.dialog

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.mei.dialog.adapter.NewPeopleGiftBagAdapter
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.Cxt
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.bag.NewPeopleGiftBagRequest

/**
 *
 * @author Created by lenna on 2020/11/16
 * 新人礼包
 */
fun MeiCustomBarActivity.showPeopleGiftBagDialog() {
    val customView = layoutInflaterKt(R.layout.dialog_new_people_gift_bag)
    val dialog = NormalDialogLauncher.newInstance()
    customView.apply {
        findViewById<ImageView>(R.id.close_new_people_gift_bag).setOnClickListener {
            dialog.dismiss()
        }
    }
    showLoading(true)
    apiSpiceMgr.executeKt(NewPeopleGiftBagRequest(), success = {
        if (it.isSuccess) {
            customView.apply {
                findViewById<ImageView>(R.id.new_people_gift_bag_bg).apply {
                    glideDisplay(it.data.background.orEmpty())
                }
                findViewById<ImageView>(R.id.new_people_gift_bag_line).apply {
                    glideDisplay(it.data.line.orEmpty())
                }
                findViewById<ImageView>(R.id.buy_now_bg).apply {
                    glideDisplay(it.data.button.orEmpty())
                }
                findViewById<RecyclerView>(R.id.new_people_gift_bag_coupon_rv)?.apply {
                    adapter = NewPeopleGiftBagAdapter(it.data.bag?.resources ?: arrayListOf())
                }
                findViewById<TextView>(R.id.buy_now).apply {
                    text = it.data.bag?.buyButtonText?.createSplitSpannable(Cxt.getColor(R.color.color_9C5300))
                }

                findViewById<View>(R.id.buy_now_fl).apply {
                    isVisible = it.data.bag?.buyButtonText != null
                    setOnClickNotDoubleListener(tag = "buyNowF") { _ ->
                        it.data.bag?.let { bag ->
                            if (bag.errorMsg?.isNotEmpty() == true) {
                                UIToast.toast(bag.errorMsg.orEmpty())
                            } else {
                                showPayExtDialog(bag, it.data.buyToast.orEmpty())
                            }
                        }
                        dialog.dismiss()
                    }
                }

            }
            dialog.showDialog(this, DialogData(customView = customView, canceledOnTouchOutside = false, backCanceled = false, key = "PeopleGiftBagDialog"), back = null)
        } else {
            UIToast.toast(it.errMsg)
        }
    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = {
        showLoading(false)
    })

}