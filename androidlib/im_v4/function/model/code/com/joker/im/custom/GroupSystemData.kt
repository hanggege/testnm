package com.joker.im.custom

import android.text.TextUtils
import com.joker.im.imLoginId

/**
 * Created by 杨强彪 on 2017/4/20.
 * @描述：群通知
 */


// servers
val kHXMessageExtType_GroupInvitation = 1000         // 透传  邀请入群         只有学员可收到
val kHXMessageExtType_GroupInvitationAccepted = 1001   // 透传  接受入群邀请   只有导师可以收到
val kHXMessageExtType_GroupApply = 1002         // 透传  申请入群               只有导师可以收到
val kHXMessageExtType_GroupApplyAccepted = 1003        // 透传  批准入群申请
val kHXMessageExtType_GroupQuit = 1004         // 透传  主动退群
val kHXMessageExtType_GroupKickout = 1005         // 透传  踢出群聊
val kHXMessageExtType_GroupApplyRejected = 1006        // 透传  拒绝入群申请

class GroupSystemData : CustomBaseData() {
    class Result : CustomInfo<GroupSystemData>()

    var group_notification_type: String = ""//群通知类型

    var number_of_kicked_out: String = ""//被踢出群次数
    var msg_id: String = ""//消息id
    var im_id: String = ""//聊天id
    var state: String = ""   //int型处理状态  // 状态区别参数，0，待处理 1，已同意，-1，已忽略"
    var admin_id: String = ""//管理员id
    var admin_name: String = ""//管理员名称
    var team_name: String = ""//导师团队名称
    var group_id: String = ""//群ID
    var group_name: String = ""//群名称
    var group_avatar: String = ""//群头像
    var user_id: String = ""//用户id
    var user_name: String = ""//用户名
    var user_avatar: String = ""//用户头像


    fun hxDescription(): String {
        return when (toInt(group_notification_type)) {
            kHXMessageExtType_GroupInvitation -> getGroupInvitationDescription()//暂未支持类型
            kHXMessageExtType_GroupInvitationAccepted -> getGroupInvitationAcceptedDescription()//暂未支持类型
            kHXMessageExtType_GroupApply -> getGroupApplyDescription()//暂未支持类型
            kHXMessageExtType_GroupApplyAccepted -> getGroupApplyAcceptedDescription()//暂未支持类型
            kHXMessageExtType_GroupQuit -> getGroupQuitDescription()//暂未支持类型
            kHXMessageExtType_GroupKickout -> getGroupKickoutDescription()//暂未支持类型
            kHXMessageExtType_GroupApplyRejected -> getGroupApplyRejectedDescription()//暂未支持类型
            else -> ""
        }

    }

    private fun toInt(int: String): Int {
        return try {
            Integer.parseInt(int)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    private fun getGroupQuitDescription(): String {
        if (TextUtils.isEmpty(user_id)) {
            return ""
        }
        return if (user_id == imLoginId()) {
            //学员
            "你已退出 $group_name"
        } else {
            // 导师
            "$user_name 退出了 $group_name"
        }

    }

    private fun getGroupApplyRejectedDescription(): String {
        if (TextUtils.isEmpty(user_id)) {
            return ""
        }
        return if (user_id == imLoginId()) {
            //学员
            "$admin_name 拒绝让你加入 $group_name"
        } else {
            // 导师
            "$user_name 已被拒绝加入 $group_name"
        }

    }

    private fun getGroupKickoutDescription(): String {

        if (TextUtils.isEmpty(user_id)) {
            return ""
        }
        return if (user_id == imLoginId()) {
            //学员
            "群内太火爆，你已被挤出 $group_name"
        } else {
            // 导师
            "$user_name 已被移出 $group_name"
        }

    }

    private fun getGroupApplyAcceptedDescription(): String {
        if (TextUtils.isEmpty(user_id)) {
            return ""
        }
        return if (user_id == imLoginId()) {
            //学员
            "$admin_name 同意让你加入 $group_name}"
        } else {
            // 导师
            "$user_name 已加入到 $group_name"
        }
    }

    private fun getGroupApplyDescription(): String {
        return "$user_name 申请加入 $group_name"
    }

    private fun getGroupInvitationAcceptedDescription(): String = "$user_name 同意加入 $group_name"

    private fun getGroupInvitationDescription(): String = "$admin_name 邀请你加入 $group_name"


    override val summary: String
        get() = hxDescription()
    override val copySummary: String
        get() = hxDescription()
}