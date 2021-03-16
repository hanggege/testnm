package com.mei.me.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.base.ui.nav.Nav
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.login.checkLogin
import com.mei.me.activity.PersonalInformationActivityLauncher
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.user.MyPageInfo

/**
 * Created by hang on 2020/7/24.
 */
class MineTabHeadHolder(view: View) : MultiViewHolder(view) {
    private var userInfo = MyPageInfo.Info()
    private val context = itemView.context

    init {
        getView<TextView>(R.id.level_text).setOnClickListener {
            Nav.toAmanLink(context, userInfo.enterLevelPage.orEmpty())
        }
        getView<ImageView>(R.id.icon_edit_studio).setOnClickListener {
            if (context.checkLogin()) {
                PersonalInformationActivityLauncher.startActivity(context)
            }
        }
        getView<ImageView>(R.id.image_avatar).setOnClickListener {
            if (context.checkLogin()) {
                if (userInfo.groupId > 0 || !userInfo.isPublisher) {
                    PersonalInformationActivityLauncher.startActivity(context)
                } else {
                    Nav.toPersonalPage(context, userInfo.userId)
                }
            }
        }
        itemView.setOnClickListener {
            if (context.checkLogin() && userInfo.groupId <= 0) {
                if (userInfo.isPublisher) {
                    Nav.toPersonalPage(context, userInfo.userId)
                } else {
                    PersonalInformationActivityLauncher.startActivity(context)
                }
            }
        }
    }

    override fun refresh(data: Any?) {
        (data as? MyPageInfo.Info)?.let { info ->
            userInfo = info

            setVisible(R.id.tab_me_enter_login, info.userId <= 0)

            refreshUi(info)
        }
    }

    private fun refreshUi(info: MyPageInfo.Info) {
        getView<RoundImageView>(R.id.image_avatar).apply {
            updateLayoutParams<ConstraintLayout.LayoutParams> {
                topMargin = dip(32.5f) + getStatusBarHeight()
            }
            glideDisplay(info.avatar, (if (JohnUser.getSharedUser().hasLogin()) info.gender else -1).genderAvatar(info.isPublisher))
        }

        setVisible(R.id.icon_role_label, info.isPublisher && info.groupId == 0)
        setVisible(R.id.text_role_label, info.groupId > 0)
        setVisible(R.id.icon_edit_studio, info.userId > 0)

        setText(R.id.text_role_label, info.groupRoleName.orEmpty())

        setText(R.id.nick_name, info.nickname.orEmpty())
        setText(R.id.level_text, info.userLevel.toString())

        getView<TextView>(R.id.level_text).apply {
            isVisible = info.userLevel > 0 && info.groupId <= 0
            text = info.userLevel.toString()
            setPadding(0, 0, if (info.userLevel > 9) dip(5) else dip(9), 0)
            setBackgroundResource(getBackGroundLevelResource(info.userLevel, LevelSize.Normal))
        }
        setText(R.id.text_id, "ID: ${info.userId}")
        setVisible(R.id.text_id, info.userId > 0)
        setVisible(R.id.nick_name, info.userId > 0)

    }

}
