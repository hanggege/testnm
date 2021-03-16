package com.mei.me.activity

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.view.isVisible
import com.joker.im.custom.chick.InviteJoinInfo
import com.mei.base.ui.nav.Nav
import com.mei.init.spiceHolder
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomSelectFragment
import com.mei.orc.ext.dip
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.chick.tab.isShowShieldingBtn
import com.net.network.chick.user.InvisibleSetRequest
import kotlinx.android.synthetic.main.me_setting_activity.*

/**
 *
 * @author Created by Ling on 2019-07-12
 * 设置页面
 */

class MeSettingActivity : MeiCustomBarActivity(), ISystemInviteJoinShow {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.me_setting_activity)

        title = "设置"
        initView()
    }


    private fun initView() {
        stealth_function_layout.isVisible = MeiUser.getSharedUser().extra?.canSetRoomInvisible == 1
        logout_btn.isVisible = JohnUser.getSharedUser().hasLogin()
        logout_btn.setOnClickNotDoubleListener { onClickLogout() }
        about_us_row.setOnClickNotDoubleListener { onClickAboutUs() }
        setting_user_privacy.setOnClickNotDoubleListener {
            //用户隐私条款
            Nav.toWebActivity(this, AmanLink.NetUrl.privacy_page, "服务及隐私协议")
        }

        new_message_notification_setting_rl.setOnClickListener {
            //跳转到新消息提醒设置页面
            startActivity(Intent(activity, NewMessageNotificationSettingActivity::class.java))
        }
        stealth_function_switch.isChecked = MeiUser.getSharedUser().info?.roomInvisible == 1
        stealth_function_switch.setOnCheckedChangeListener { _, isChecked ->
            MeiUser.getSharedUser().info?.roomInvisible = if (isChecked) 1 else 0
            //  上报给服务器隐身状态
            spiceHolder().apiSpiceMgr.executeKt(InvisibleSetRequest(isChecked), finish = {
                MeiUser.resetUser {
                    if (!isDestroyed) {
                        stealth_function_switch.isChecked = MeiUser.getSharedUser().info?.roomInvisible == 1
                    }
                }
            })
        }
        my_blacklist_row.setOnClickNotDoubleListener {
            startActivity(Intent(activity, MyBlacklistActivity::class.java))
        }
        my_blacklist_row.isVisible = isShowShieldingBtn() && JohnUser.getSharedUser().hasLogin()
    }

    private fun onClickAboutUs() {
        Nav.toAmanLink(this, AmanLink.NetUrl.about_url)
    }

    private fun onClickLogout() {
        BottomSelectFragment()
                .setBgColor(ColorDrawable(Color.parseColor("#999999")))
                .addItem(getString(R.string.exit))
                .setFooter("取消", textSize = 16f, textColor = Color.parseColor("#9b9b9b"), height = dip(42))
                .setItemClickListener(Callback {
                    when (it.type) {
                        0 -> {
                            // 退出登录
                            Nav.logout(activity, false)
                        }
                    }
                })
                .show(supportFragmentManager, this::class.java.simpleName)
    }

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean = false

}