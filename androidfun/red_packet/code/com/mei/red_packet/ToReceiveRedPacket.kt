package com.mei.red_packet

import androidx.fragment.app.FragmentActivity
import com.chuanglan.shanyan_sdk.OneKeyLoginManager
import com.mei.base.common.BIND_PHONE_ACTION
import com.mei.base.network.holder.SpiceHolder
import com.mei.init.spiceHolder
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.login.finishShanYanAuth
import com.mei.orc.event.postAction
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.clearDoubleCheck
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.red_packet.view.provideShanYanBindPhoneConfig
import com.mei.shanyan.openLoginAuth
import com.mei.wood.extensions.executeKt
import com.mei.wood.rx.MeiUiFrame
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.clickRedPacketDialogStat
import com.mei.wood.ui.redPacketBrowseDialog
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import com.net.network.chick.user.ShanYanUpdatePhoneRequest

/**
 *
 * @author Created by lenna on 2020/7/24
 * 领取红包入口事件：如果没有绑定手机号，需要弹起绑定手机号dialog ；如果已绑定手机号，则显示当前剩余多少时间可以领取红包逻辑
 */
private var shanYanStarting = false
fun MeiCustomActivity.toReceiveRedPacket(back: (Boolean) -> Unit = { _ -> }, openResult: (Boolean) -> Unit = {}): Boolean {

    if (shanYanStarting) return false
    return if (isOnDoubleClick(1500, "BindPhoneTag")) {
        false
    } else {
        (this as? MeiUiFrame)?.showLoading(true)
        shanYanStarting = true
        openLoginAuth(provideShanYanBindPhoneConfig {
            shanYanStarting = false
        }, openFailure = {
            openResult(false)
            shanYanStarting = false
            (this as? MeiUiFrame)?.showLoading(false)
            (this as? FragmentActivity)?.showBindPhoneDialog(back)
        }, openSuccess = {
            openResult(true)
            shanYanStarting = false
            (this as? MeiUiFrame)?.showLoading(false)
            (this as? MeiCustomActivity)?.redPacketBrowseDialog()
        }) { code, result ->
            //闪验回调
            when (code) {
                0 -> back(false)
                -1 -> {//数据有问题
                    finishShanYanAuth()
                    UIToast.toast("数据返回错误")
                    clearDoubleCheck("BindPhoneTag")
                    (this as? FragmentActivity)?.showBindPhoneDialog(back)
                }
                1 -> {
                    //授权成功，绑定手机号，并领取红包
                    OneKeyLoginManager.getInstance().setLoadingVisibility(true)
                    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
                    //调用后端接口进行手机绑定
                    apiClient.executeKt(ShanYanUpdatePhoneRequest(result, if (this is VideoLiveRoomActivity) 1 else 2), success = {
                        if (it.isSuccess) {
                            MeiUser.resetUser(apiClient, object : RequestListener<ChickUserInfo.Response> {
                                override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
                                    finishShanYanAuth()
                                    clearDoubleCheck("BindPhoneTag")
                                    (activity as? FragmentActivity)?.showBindPhoneDialog(back)
                                }

                                override fun onRequestSuccess(result: ChickUserInfo.Response) {
                                    showReceiveRedPacketDialog()
                                    back(true)
                                    postAction(BIND_PHONE_ACTION)
                                }

                            })

                        } else {
                            UIToast.toast(it.msg)
                            finishShanYanAuth()
                            clearDoubleCheck("BindPhoneTag")
                            (this as? FragmentActivity)?.showBindPhoneDialog(back)
                        }
                    }, failure = {
                        UIToast.toast(it?.currMessage)
                        finishShanYanAuth()
                        clearDoubleCheck("BindPhoneTag")
                        (this as? FragmentActivity)?.showBindPhoneDialog(back)
                    }, finish = {
                        finishShanYanAuth()
                    })
                    clickRedPacketDialogStat("一键领取")
                }
            }

        }
        true
    }

}

