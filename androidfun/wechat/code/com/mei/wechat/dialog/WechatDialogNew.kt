package com.mei.wechat.dialog

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.core.text.toSpannable
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.joker.support.TdSdk
import com.mei.base.google.contact.isShieldNoPay
import com.mei.base.ui.nav.Nav
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.init.spiceHolder
import com.mei.orc.Cxt
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.UIRequestListener
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.app.copyToClipboard
import com.mei.wechat.MainAppGNData
import com.mei.wechat.data.WechatData
import com.mei.wechat.listener.WechatListener
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.buildLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.MeiUtil
import com.net.model.wechat.WeChat_Number_Response
import com.net.network.wechat.WeChat_Add_Request
import com.net.network.wechat.WeChat_Copy_Request
import kotlinx.android.synthetic.main.open_weixin_add_layout_new.*
import launcher.ParentCls

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/7
 * modify by Ling on 2018/6/25，优化微信UI，并进行AB测 暂时全量切换成B版，由服务器控制，看线上效果
 */
@ParentCls
class WechatDialogNew : MeiSupportDialogFragment() {
    private var wechatNumber: WeChat_Number_Response? = null
    private var wechatData: WechatData? = null
    private var mainAppGNData: MainAppGNData? = null

    private var numberCallBack: WechatListener<WeChat_Number_Response?>? = null


    override fun onSetInflaterLayout() = R.layout.open_weixin_add_layout_new

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (wechatNumber == null) dismissAllowingStateLoss()

        /**b版加微信弹窗**/
//            new_risk_tips.isVisible = !isShieldNoPay
        new_risk_tips.isInvisible = true

        wechatNumber?.let { weChat_number ->
            avatar_img.glideDisplay(weChat_number.avatar, GlideDisPlayDefaultID.default_avatar_50)
            mentor_wechat.text = weChat_number.weixin_id

            before_add_wechat.isInvisible = false
            loading_layout.isVisible = false
            add_wechat_success.isVisible = false

            new_risk_tips.movementMethod = LinkMovementMethod.getInstance()
            if (MeiUtil.isOfficialID(wechatData?.mentor_id ?: 0)) {
                new_risk_tips.text = getString(R.string.add_wechat_rist_tips_official)
            } else {
                new_risk_tips.text = getString(R.string.add_wechat_rist_tips)
                        .toSpannable().buildLink("点此举报 >") {
                            dismissAllowingStateLoss()
                            /**跳转客服中心*/
                            Nav.toAmanLink(activity, AmanLink.NetUrl.customer_service_center)
                        }
            }
            add_wechat.setOnClickListener {
                activity.copyToClipboard(weChat_number.weixin_id)
                before_add_wechat.isInvisible = true
                loading_layout.isVisible = true
                runDelayedOnUiThread(700) {
                    loading_layout?.isVisible = false
                    add_wechat_success?.isVisible = true
                }
                runDelayedOnUiThread(1700) {
                    activity?.let {
                        TdSdk.openWeixin(activity)
                        wechatData?.let {
                            spiceHolder().apiSpiceMgr.executeKt(WeChat_Copy_Request(it.mentor_id, 0, false))
                        }
                    }
                }
            }
        }

    }


    override fun dismissAllowingStateLoss() {
        super.dismissAllowingStateLoss()
        wechatNumber = null
        numberCallBack = null
    }

    override fun dismiss() {
        super.dismiss()
        wechatNumber = null
        numberCallBack = null
    }

    /**
     * 添加微信
     */
    private fun showWXNumber(manager: FragmentManager, info: WeChat_Number_Response) {
        this.wechatNumber = info
        show(manager, WechatDialogNew::class.java.simpleName)
    }

    fun showWXNumber(manager: FragmentActivity?, mentor_id: Int, from_type: String, from_id: String, mainAppGNData: MainAppGNData? = null) {
        val data = WechatData(mentor_id, from_type, from_id)
        showWXNumber(manager, data, null, mainAppGNData)
    }

    @JvmOverloads
    fun showWXNumber(activity: FragmentActivity?, data: WechatData, callback: WechatListener<WeChat_Number_Response?>? = null, mainAppGNData: MainAppGNData? = null) {
        this.mainAppGNData = mainAppGNData
        activity?.let { at ->
            this.wechatData = data
            this.numberCallBack = callback
            (at as? LoadingToggle)?.showLoading(true)
            requestGetWeChat(at, data)
        }
    }

    private fun requestGetWeChat(activity: FragmentActivity, data: WechatData) {
        spiceHolder().apiSpiceMgr.execute(WeChat_Add_Request(data.mentor_id, data.from_type, data.from_id),
                object : UIRequestListener<WeChat_Number_Response.Response>() {
                    override fun onRequestSuccess(result: WeChat_Number_Response.Response) {
                        (activity as? LoadingToggle)?.showLoading(false)

                        if (result.isSuccess && result.data != null) {
                            isShieldNoPay = result.data.review_version
                            showWXNumber(activity.supportFragmentManager, result.data)
                        } else {
                            val errorRtn = result.getRtn()

                            val message = result.errMsg
                                    ?: "Unknown message with code: $errorRtn"
                            if (errorRtn == 31201) { // 胡梅要求在导师没有配置微信的时候特殊toast提示
                                UIToast.toast(Cxt.get(), "该咨询师未配置微信号")
                            } else {
                                UIToast.toast(Cxt.get(), message)
                            }
                        }
                    }

                    override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
                        super.onRequestFailure(retrofitThrowable)
                        (activity as? LoadingToggle)?.showLoading(false)
                    }
                })
    }

}