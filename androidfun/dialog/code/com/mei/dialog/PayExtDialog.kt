package com.mei.dialog

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.joker.PayFlags
import com.joker.PayManager
import com.joker.PayType
import com.joker.connect.PayCallBack
import com.joker.model.PayFailure
import com.joker.model.PaySuccess
import com.joker.model.order.Order
import com.mei.GrowingUtil
import com.mei.base.common.NEW_PEOPLE_GIFT_BAG_PAY_SUCCESS
import com.mei.dialog.adapter.PayExtContentListAdapter
import com.mei.init.spiceHolder
import com.mei.orc.event.postAction
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.bag.NewPeopleGiftBagInfo
import com.net.network.chick.pay.GiftBagPayFinishRequest
import com.net.network.chick.pay.NewPeopleGiftBagOrderCreateRequest

/**
 *
 * @author Created by lenna on 2020/11/17
 * 显示新人礼包支付详情页面
 */

fun MeiCustomBarActivity.showPayExtDialog(bag: NewPeopleGiftBagInfo.GiftBag, buyToast: String) {
    val customView = layoutInflaterKt(R.layout.view_pay_ext)
    val dialog = NormalDialogLauncher.newInstance()
    val list = arrayListOf<NewPeopleGiftBagInfo.GiftBagResources>()
    list.clear()
    list.addAll(bag.resources ?: arrayListOf())
    customView.apply {
        findViewById<ImageView>(R.id.close_dialog).setOnClickListener {
            dialog.dismiss()
        }
        findViewById<TextView>(R.id.pay_ext_subtitle).apply {
            text = bag.bagName.orEmpty()
            paint.isFakeBoldText = true
        }
        findViewById<RecyclerView>(R.id.gift_bag_rlv).apply {
            adapter = PayExtContentListAdapter(list)
        }
        findViewById<View>(R.id.we_chat_pay).apply {
            setOnClickNotDoubleListener(tag = "weChatPay") {
                toPay(bag, buyToast, PayType.weixin, PayFromType.NEW_PEOPLE_GIFT_BAG, callBack = {
                    if (it) {
                        dialog.dismiss()
                    }
                })
            }
        }
        findViewById<View>(R.id.ali_pay).apply {
            setOnClickNotDoubleListener(tag = "aLiPay") {
                toPay(bag, buyToast, PayType.alipay, PayFromType.NEW_PEOPLE_GIFT_BAG, callBack = {
                    if (it) {
                        dialog.dismiss()
                    }
                })
            }
        }
    }
    dialog.showDialog(this, DialogData(customView = customView, canceledOnTouchOutside = false, backCanceled = false, key = "PayExtDialog"), back = {

    })
}

/**
 * 去支付
 */
fun FragmentActivity.toPay(bag: NewPeopleGiftBagInfo.GiftBag, buyToast: String, @PayFlags payType: Int, fromType: PayFromType, callBack: (paySuccess: Boolean) -> Unit = {}) {
    (this as? MeiCustomBarActivity)?.getOrder(bag.seqId.toString(), fromType, payType, back = { order ->
        order?.apply {
            PayManager.performPay(this@toPay, payType, this, object : PayCallBack {
                override fun onSuccess(success: PaySuccess) {
                    UIToast.toast(buyToast)
                    success.orderSn?.let {
                        spiceHolder().apiSpiceMgr.executeKt(GiftBagPayFinishRequest(it), finish = {
                            postAction(NEW_PEOPLE_GIFT_BAG_PAY_SUCCESS)
                        })
                    }
                    GrowingUtil.track("pm_successful", "amount", bag.price.toString())
                    callBack(true)
                }

                override fun onFailure(failure: PayFailure?) {
                    UIToast.toast(failure?.err_msg ?: "支付失败")
                    callBack(false)
                }
            })
        }
    })
}

/**
 * 获取订单
 */
fun MeiCustomBarActivity.getOrder(bagId: String, fromType: PayFromType, @PayFlags payType: Int, back: (Order?) -> Unit = { }) {
    apiSpiceMgr.executeToastKt(NewPeopleGiftBagOrderCreateRequest(bagId, fromType, payType), success = { response ->
        if (response.isSuccess && response.data != null) {
            back(response.data.getOrder(payType))
        } else {
            back(null)
        }
    }, failure = {
        back(null)
    })
}