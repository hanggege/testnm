package com.mei.wood.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.joker.support.TdSdk
import com.mei.base.saveImgToGallery
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.wood.R
import kotlinx.android.synthetic.main.dialog_open_wechat.*
import launcher.ParentCls

/**
 * Created by hang on 2019-11-12.
 * 复制客服微信弹窗
 */
@ParentCls
class OpenWeChatDialog : MeiSupportDialogFragment() {

    private var weChatCodeImg = ""

    override fun onSetInflaterLayout(): Int {
        return R.layout.dialog_open_wechat
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wechat_code.glideDisplay(weChatCodeImg)

        wechat_code.setOnLongClickListener {
            context?.saveImgToGallery(weChatCodeImg)
            true
        }

        open_wechat.setOnClickListener {
            activity?.let {
                TdSdk.openWeixin(activity)
            }
            dismissAllowingStateLoss()
        }
    }

    fun showWXNumber(fragmentActivity: FragmentActivity?) {
        requestWXNumber(fragmentActivity)
    }

    private fun requestWXNumber(fragmentActivity: FragmentActivity?) {
        (fragmentActivity as? LoadingToggle)?.showLoading(true)
//        (fragmentActivity as? SpiceHolder)?.apiSpiceMgr?.executeKt(Get_customer_service_weixin_Request(), success = {
//            if (it.isSuccess) {
//                weChatCodeImg = it.data?.customer_service_weixin_url ?: ""
//
//                show(fragmentActivity.supportFragmentManager, OpenWeChatDialog::class.java.simpleName)
//            } else {
//                UIToast.toast(it.errMsg)
//            }
//        }, failure = {
//            UIToast.toast(it?.currMessage)
//        }, finish = {
//            (fragmentActivity as? LoadingToggle)?.showLoading(false)
//        })
    }
}