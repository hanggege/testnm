package com.mei.work.adapter.item

import android.graphics.Color
import android.view.View
import android.widget.ImageView
import com.mei.chat.toImChat
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.holder.activity
import com.mei.widget.round.RoundView
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.extensions.executeKt
import com.net.MeiUser
import com.net.model.chick.friends.UserHomePagerResult
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.workroom.SendAnalystsRequest

/**
 * WorkRoomAvatarHolder
 *
 * 工作室成员头像
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-25
 */
class WorkRoomAvatarHolder(view: View) : WorkRoomBaseHolder(view) {

    var consultInfo: UserHomePagerResult.ConsultInfo = UserHomePagerResult.ConsultInfo()

    init {
        view.setOnClickNotDoubleListener(tag = "work_room_consult") {
            if (MeiUser.getSharedUser().info?.groupRole ?: 0 > 0 || MeiUser.getSharedUser().info?.isPublisher == true) {
                UIToast.toast("暂不支持知心达人之间的私聊功能")
                return@setOnClickNotDoubleListener
            }
            showLoading(true)
            apiSpiceMgr.executeKt(SendAnalystsRequest(consultInfo.bindPromoterId, consultInfo.userId), success = {
                if (it.isSuccess) {
                    apiSpiceMgr.requestImUserInfo(arrayOf(consultInfo.bindPromoterId), needRefresh = true, back = {
                        val userInfo = it.firstOrNull()
                        when {
                            userInfo?.isSelfToBlackList == true -> {
                                UIToast.toast(tabbarConfig.groupBlacklistUser)
                            }
                            userInfo?.isTheOtherToBlackList == true -> {
                                UIToast.toast(tabbarConfig.hasBlacklistTips)
                            }
                            else -> {
                                activity?.toImChat(consultInfo.bindPromoterId.toString())
                            }
                        }
                    })
                }
            }, failure = {
                activity?.toImChat("")
            }, finish = {
                showLoading(false)
            })
        }
    }

    override fun refresh(item: Any) {
        if (item is WorkRoomAvatarData) {
            consultInfo = item.consultInfo
            getView<ImageView>(R.id.room_main_square_avatar).glideDisplay(consultInfo.personalImage.orEmpty(), consultInfo.gender.genderAvatar())
            setText(R.id.room_main_square_avatar_text, consultInfo.nickname)
            getView<RoundView>(R.id.room_main_square_avatar_bg).setBackColor(consultInfo.backgroundColor.parseColor(Color.WHITE))
        }
    }
}

class WorkRoomAvatarData(var consultInfo: UserHomePagerResult.ConsultInfo, var color: AvatarBg)

enum class AvatarBg {
    BROWN, DARK_BLUE, PURPLE, LIGHT_BLUE
}