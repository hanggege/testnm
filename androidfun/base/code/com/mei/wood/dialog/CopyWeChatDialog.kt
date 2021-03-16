package com.mei.wood.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.joker.support.TdSdk
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ui.loading.LoadingToggle
import com.mei.orc.util.app.copyToClipboard
import com.mei.wood.R
import kotlinx.android.synthetic.main.dialog_copy_wechat.*
import launcher.ParentCls

/**
 * Created by hang on 2019-11-12.
 * 复制客服微信弹窗
 */
@ParentCls
class CopyWeChatDialog : MeiSupportDialogFragment() {

    private var weChatNum = ""

    override fun onSetInflaterLayout(): Int {
        return R.layout.dialog_copy_wechat
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        wechat_number.text = weChatNum
        copy_wechat.setOnClickListener {
            activity?.let {
                it.copyToClipboard(weChatNum)
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
//                weChatNum = it.data?.customer_service ?: ""
//
//                show(fragmentActivity.supportFragmentManager, CopyWeChatDialog::class.java.simpleName)
//            }else{
//                UIToast.toast(it.errMsg)
//            }
//        }, failure = {
//            UIToast.toast(it?.currMessage)
//        },finish = {
//            (fragmentActivity as? LoadingToggle)?.showLoading(false)
//        })
    }
}