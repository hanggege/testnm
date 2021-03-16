package com.mei.login

import android.view.View
import android.widget.TextView
import com.mei.base.common.CHANG_NICKNAME
import com.mei.live.manager.genderAvatar
import com.mei.orc.event.postAction
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.TabMainActivity
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import com.net.model.user.UserInfo
import com.net.network.chick.user.GetNicknameAndUpdateRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/2
 */
var isLoginByRegistered = false
fun TabMainActivity.checkLoginRegistered() {
    runDelayedOnUiThread(2000) {
        if (isLoginByRegistered) {
            requestMeiUserInfo {
                if (isLoginByRegistered) {
                    it?.let { info ->
                        var dialog: NormalDialog? = null
                        val customView = layoutInflaterKt(R.layout.registered_welcome_dialog).apply {
                            findViewById<RoundImageView>(R.id.user_avatar_img).glideDisplay(info.avatar.orEmpty(), info.gender.genderAvatar(info.isPublisher))
                            findViewById<TextView>(R.id.user_name_tv).apply {
                                text = "昵称：${info.nickname}"
                            }
                            findViewById<TextView>(R.id.welcome_title).paint.isFakeBoldText = true
                            findViewById<View>(R.id.welcome_submit_tv).setOnClickListener { dialog?.dismissAllowingStateLoss() }
                            findViewById<View>(R.id.welcome_label_change_nickname).setOnClickNotDoubleListener(tag = "change_nickname") {
                                apiSpiceMgr.executeKt(GetNicknameAndUpdateRequest(), success = { response ->
                                    if (response.isSuccess && !response.data.value.isNullOrEmpty()) {
                                        findViewById<TextView>(R.id.user_name_tv).text = "昵称：${response.data.value}"
                                        postAction(CHANG_NICKNAME, response.data.value)
                                        UIToast.toast("昵称修改成功")
                                    }
                                })
                            }
                        }
                        dialog = NormalDialog().showDialog(this, DialogData(customView = customView, canceledOnTouchOutside = false), back = {

                        })
                    }
                }
                isLoginByRegistered = false
            }
        }
    }
}

private fun TabMainActivity.requestMeiUserInfo(back: (UserInfo?) -> Unit) {
    if (!JohnUser.getSharedUser().hasLogin()) {
        back(null)
    } else if (MeiUser.getSharedUser().info?.userId == JohnUser.getSharedUser().userID) {
        back(MeiUser.getSharedUser().info)
    } else {
        MeiUser.resetUser(apiSpiceMgr, object : RequestListener<ChickUserInfo.Response> {
            override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
                back(null)
            }

            override fun onRequestSuccess(result: ChickUserInfo.Response) {
                back(result.data?.info)
            }
        })
    }
}