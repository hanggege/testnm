package com.mei.dialog

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.joker.PayFlags
import com.joker.PayManager
import com.joker.PayType
import com.joker.connect.PayCallBack
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.LiveGift
import com.joker.im.custom.chick.SplitText
import com.joker.model.PayFailure
import com.joker.model.PaySuccess
import com.joker.model.order.Order
import com.mei.GrowingUtil
import com.mei.base.network.holder.SpiceHolder
import com.mei.init.spiceHolder
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.fragment.RECHARGE_SUCCESS_ID
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.rx.MeiUiFrame
import com.net.MeiUser
import com.net.model.chick.pay.ProductBean
import com.net.model.chick.pay.ProductListResult
import com.net.model.rose.RoseFromSceneEnum
import com.net.network.chick.pay.OrderCreateRequest
import com.net.network.chick.pay.PayFinishRequest
import com.net.network.chick.pay.ProductListRequest
import com.net.network.rose.MyRoseInfoRequest
import kotlinx.android.synthetic.main.view_pay.*

/**
 *  Created by zzw on 2019-11-29
 *  Des: 充值对话框
 */

/**
 * 获取我的钱
 */
fun FragmentActivity?.myRose(back: (roseCount: Double/* -1表示查询失败 */) -> Unit = {}) {
    if (this == null) {
        Log.e("myRose", "activity is null")
        back(-1.0)
        return
    }

    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    apiClient.executeKt(MyRoseInfoRequest(), success = {
        if (it.isSuccess && it.data != null && it.data.coin != null) {
            MeiUser.getSharedUser().extra?.coinBalance = it.data.coin.coinBalance
            back(it.data.coin.coinBalance)
        } else {
            back(-1.0)
        }
    }, failure = {
        back(-1.0)
    })
}

/**
 * from = 0表示从首页进入
 * from = 1表示直播间进入
 */
@JvmOverloads
fun FragmentActivity?.showPayDialog(fromType: PayFromType, height: Int = -1, callBack: (paySuccess: Boolean) -> Unit = { }) {
    if (this == null) {
        Log.e("showPayDialog", "activity is null")
        callBack(false)
        return
    }
    (this as? MeiUiFrame)?.showLoading(true)
    val isStudio = (this as? VideoLiveRoomActivity)?.roomInfo?.isStudio == true
    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    apiClient.executeKt(ProductListRequest().apply {
        showAll = 1
        textType = if (isStudio) 1 else 0
    }, success = {
        if (it.isSuccess && it.data != null && !this.isFinishing) {
            PayDialog().apply {
                this.data = it.data
                this.fromType = fromType
                this.callBack = callBack
                this.height = height
            }.show(supportFragmentManager, "PayDialog")
        } else {
            callBack(false)
            UIToast.toast(it.msg)
        }
    }, failure = {
        callBack(false)
        UIToast.toast(it?.currMessage ?: "请求失败")
    }, finish = {
        (this as? MeiUiFrame)?.showLoading(false)
    })


}


class PayDialog : BottomDialogFragment() {

    var fromType: PayFromType = PayFromType.HOME_PAGE

    var data: ProductListResult? = null
    var height : Int = -1
    var callBack: (paySucc: Boolean) -> Unit = { }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_pay, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.setCanceledOnTouchOutside(false)

        if (data == null) {
            Log.e("PayDialog", "data is null")
            dismissAllowingStateLoss()
            callBack(false)
            return
        }

        if (height != -1) {
            pay_dialog_root.updateLayoutParams<LinearLayout.LayoutParams> {
                height = this@PayDialog.height
                weight = 0f
            }
        }

        pay_top_view.setOnClickListener {
            callBack(false)
            dismissAllowingStateLoss()
        }

        dialog?.setOnKeyListener(object : DialogInterface.OnKeyListener {
            override fun onKey(dialog: DialogInterface, keyCode: Int, event: KeyEvent): Boolean {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    callBack(false)
                    dismissAllowingStateLoss()
                    return true
                }
                return false
            }
        })

        data?.let {
            gift_view.setData(it)
        }

        wechat_pay.setOnClickListener {
            //微信支付
            gift_view.getSelectedGiftInfo()?.let {
                pay(it, PayType.weixin)
            }
        }

        ali_pay.setOnClickListener {
            //支付宝支付
            gift_view.getSelectedGiftInfo()?.let {
                pay(it, PayType.alipay)
            }
        }
    }

    private fun pay(productsBean: ProductBean, @PayFlags payType: Int) {
        activity?.toPay(productsBean, payType, fromType) {
            callBack(it)
            dismissAllowingStateLoss()
        }
    }
}

private fun getOrder(productId: String, fromType: PayFromType, @PayFlags payType: Int, roomId: String = "", back: (Order?) -> Unit = { }) {
    spiceHolder().apiSpiceMgr.executeToastKt(OrderCreateRequest(productId, fromType, payType, roomId), success = { response ->
        if (response.isSuccess && response.data != null) {
            back(response.data.getOrder(payType))
        } else {
            back(null)
        }
    }, failure = {
        back(null)
    })
}

/**
 * 去支付
 */
fun FragmentActivity.toPay(product: ProductBean, @PayFlags payType: Int, fromType: PayFromType, callBack: (paySuccess: Boolean) -> Unit = {}) {
    val roomId = (this as? VideoLiveRoomActivity)?.roomId ?: ""
    val createUserId = (this as? VideoLiveRoomActivity)?.roomInfo?.createUser?.userId ?: 0
    getOrder(product.productId, fromType, payType, roomId) { order ->
        order?.apply {
            PayManager.performPay(this@toPay, payType, this, object : PayCallBack {
                override fun onSuccess(success: PaySuccess) {
                    success.orderSn?.let {
                        spiceHolder().apiSpiceMgr.executeKt(PayFinishRequest(it))
                    }
                    myRose { nowMoney ->
                        if (nowMoney > -1) {
                            UIToast.toast("支付成功，当前心币数： ${MeiUser.getSharedUser().extra?.coinBalance?.toInt() ?: 0}")
                        }
                    }
                    (this@toPay as? VideoLiveRoomActivity)?.run {
                        liveSendCustomMsg(roomId, CustomType.send_gift, applyData = {
                            whitelist.add(JohnUser.getSharedUser().userID)
                            whitelist.add(roomInfo.createUser?.userId ?: 0)
                            this.roomId = roomInfo.roomId
                            gift = LiveGift().apply {
                                giftId = RECHARGE_SUCCESS_ID
                                title = "恭喜 ${MeiUser.getSharedUser().info?.nickname.orEmpty()}\n充值${product.coinNum}心币成功"
                            }
                        })
                    }
                    /**付费成功就上报一次*/
                    GrowingUtil.track("pm_successful", "amount", product.coinNum.toString())
                    callBack(true)
                }

                override fun onFailure(failure: PayFailure?) {
                    UIToast.toast(failure?.err_msg ?: "支付失败")
                    callBack(false)
                    if (roomId.isNotEmpty()) {
                        liveSendCustomMsg(roomId, CustomType.send_text, applyData = {
                            whitelist.add(createUserId)
                            val name = MeiUser.getSharedUser().info?.nickname.orEmpty()
                            content = arrayListOf<SplitText>().apply {
                                if (name.isNotEmpty()) add(SplitText(name, "#A3E2FB"))
                                add(SplitText(" 充值未完成", "#B3FFFFFF"))
                            }
                        })
                    }
                }
            })
        }
    }
}

enum class PayFromType {
    HOME_PAGE,// 首页自己进入专属房拉起的充值
    IM,//im消息
    MY_ROSE,// 我的心币页面
    BROADCAST,// 直播间
    BROADCAST_SEND_GIFT,//直播间送礼弹窗充值
    BROADCAST_FREE_COUPON,// 直播间优惠券
    BROADCAST_UP,//连线过程余额不足充值
    INVITED_UP_APPLY,//用户被主播邀请连线时余额不足时充值
    USER_PAGE,// 个人主页（从直播间进入到个人主页的也算个人主页）
    POPUP,// 系统弹窗拉起充值
    FRIEND_LIST,// 好友列表
    HD_LIST,// 互动列表
    REMINDER_POP,
    UP_APPLY,
    VOICE_UP_APPLY,//工作室申请语音连线
    IM_UP_APPLY,//im申请充值
    QUICK_CONSULT_INSUFFICIENT_BALANCE,//快速咨询余额不足
    NEW_PEOPLE_GIFT_BAG, //新人礼包
    NONE
}

fun String.parsePayFrom(): PayFromType {
    var result = PayFromType.NONE
    PayFromType.values().forEach {
        if (it.name.equals(this, true)) result = it
    }
    return result
}


fun RoseFromSceneEnum.roseFromSceneEnum2PayFromType(): PayFromType {
    return when (this) {
        RoseFromSceneEnum.COMMON_ROOM, RoseFromSceneEnum.EXCLUSIVE_ROOM -> {
            PayFromType.BROADCAST
        }
        RoseFromSceneEnum.PERSONAL_PAGE -> {
            PayFromType.USER_PAGE
        }
        RoseFromSceneEnum.EXCLUSIVE_CHAT -> {
            PayFromType.IM
        }
    }
}