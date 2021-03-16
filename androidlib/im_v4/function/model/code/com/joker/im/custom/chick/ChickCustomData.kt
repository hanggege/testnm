package com.joker.im.custom.chick

import com.joker.im.custom.CustomBaseData
import com.joker.im.custom.CustomInfo
import com.joker.im.custom.CustomType
import java.io.Serializable

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-04
 */
class ChickCustomData : CustomBaseData() {
    class Result(type: CustomType, data: ChickCustomData) : CustomInfo<ChickCustomData>(type.name, data)

    var whitelist = arrayListOf<Int>() // 消息可见性,白名单的人可见,为空全部可见,如果id在0到10之间则此id为角色id,否则为用户id
    var blacklist = arrayListOf<Int>() // 消息可见性,黑名单的人不可见,为空全部可见,如果id在0到10之间则此id为角色id,否则为用户id,白名单和黑名单同时存在交集时黑名单优先级高
    var title: String = ""
    var action: String = ""
    var seq_id = ""//上报ID
    var userId: Int = 0
    var msgState: Int = 0//消息类型，0：公开房间的消息，1：专属房间的消息,2:全部可见消息 4知心达人，助教，超管可见
    var deleteEnable = true//是否可以删除
    var content: List<SplitText> = arrayListOf()
    val uploadLog: UploadLog? = null//声网日志
    var cost_sec = 0
    var report = ""
    var status = ""
    var roomId: String = ""
    var ignoreIds = arrayListOf<Int>()
    var roomInfoChange: RoomInfoChange? = null
    var roomType = "COMMON"
    var roomTypeGrowing = ""
    var inviteJoin: InviteJoinInfo? = null
    var inviteUp: InviteUp? = null
    var roomRelated: RoomMsgData? = null
    var gift: LiveGift? = null
    var roomTypeChange: RoomTypeChange? = null
    var userLevelChange: UserLevel? = null
    var roomEnterAnim: RoomEnterAnim? = null
    var userInfoChange: UserInfoChange? = null
    var msgId: String = ""// 消息id
    var userIds: List<Int> = arrayListOf()
    var at: List<AtUserInfo> = arrayListOf()
    var serviceInfo: ServiceInfo? = null//专属服务ID
    var courseInfo: CourseInfo? = null // 课程信息
    var state: Int = 0
    var reason: Int = 0 //1:管理员踢人 2，红娘踢人，3，服务器人脸检测踢人(弹框提示)
    var mode: Int = 1 //当前窗口模式 0: 对等, 1: 主播大用户小, 2:用户大主播小
    var videoMode = 1 // 视频模式 0: 无效值兼容旧版, 1: 仅音频, 2:视频+音频
    var targetUserId = 0//目标用户id
    var publisherTips = ""//主播toast文案
    var targetTips = ""//被踢的用户文案
    var otherTips = ""//其他观众文案
    var tag = ""//品类名

    var leftText = ""
    var rightText = ""
    var timeOut: Int = 30
    var contentAlign = 1 //0 居左 1居中 2居右
    var dialogConfig: DialogConfig? = null
    var medalInfo: MoreHonorMedal? = null //荣誉勋章

    var fromUserHandle = false

    var exclusiveResult: ExclusiveResult? = null

    var apply: ApplyInfo? = null //申请排麦弹窗信息

    var card: GeneralCard? = null
    var cardPopup: GivingCourse? = null

    var pushTitle = ""
    var pushContent = ""
    var applyType = ""

    var receiveCount = 0L //榜单收到心币
    var weekRankText = "" //周榜排名
    var canPushWhenAtForeground: Boolean = true //是否前台推送（示例：如果是封禁账号当app处于前台时不显示这条横幅）

    var userOfficialAvatar = false

    var couponInfo: CouponInfo? = null

    var specialServiceInfo: SpecialServiceInfo? = null
    var couponMessageItem: CouponMessageItem? = null

    var source = ""//消息来源

    var extra: Extra? = null //目前使用到的地方（1.显示当前排麦正在使用的优惠券，版本：2.5.0）


    fun setText(string: String) {
        content = arrayListOf(SplitText(string))
    }

    override val copySummary: String
        get() = summary

    override val summary: String
        get() = content.joinToString(separator = "") { it.text }
}

class GivingCourse {
    var bgImg = ""
    var avatar = ""
    var title: List<SplitText>? = null
    var buttons = arrayListOf<SplitText>()
}

class GeneralCard {
    var title = arrayListOf<SplitText>()
    var subTitle = arrayListOf<SplitText>()
    var images = ""
    var buttons = arrayListOf<SplitText>()
    var action = ""
    var style = -1 //0:right 1:left 2:top 3:bottom
}

class DialogConfig {
    var rightCloseButton = false
    var timeCountdown = 0   //* 0:不显示, 1:左边,2:右边
    var timeoutResult = 2  //超时的处理结果 0:拒绝, 1:同意, 2:超时
    var canceledOnTouchOutside = false //是否点击外围取消弹窗
    var backCanceled = false //返回按键是否关闭
}

class ExclusiveResult {
    var status: String = "FINISHED"
    var costSec: Int = 0
    var forSender = "" //发送者展示信息
    var forReceiver = "" //接收者展示信息
    var canCallbackForReceiver = false//接受方是否可以回拨
    var canCallbackForSender = false//发送方是否可以回拨
    var special = false //是否是专属服务
}

class ApplyInfo {
    var queue = arrayListOf<ApplyUserInfo>()//申请排麦头像list
    var count = 0//申请排麦数
}

class UserInfo(val userId: Int = 0, val nickName: String = "", val gender: Int = 1, val avatar: String = "")
class ApplyUserInfo(var userId: Long = 0L, var avatar: String = "", var gender: Int = 1, var isPublisher: Boolean = false)
class AtUserInfo(var id: String = "", var userName: String = "")

class RoomTypeChange {
    val allowUser: List<Int> = arrayListOf()// 允许拉流的用户id, 为空表示没有限制
    val begin = 0//专属房开始时间
    var serviceName = ""
}

//声网log日志上传
class UploadLog {
    val logType = ""
    val token = ""
    val key = ""
    val url = ""
}

class RoomInfoChange {
    val title = ""
    val tag = ""
    var broadcastId = ""
    val roomType = ""
    var receiveCount = 0L//收到心币数
}

class RoomMsgData {
    var timeOut: Int = 10// 10s超时
    var roomId: String = ""// 房间id
    var gender: Int = 1// 用户性别
    var ignoreIds: MutableList<Int> = arrayListOf()
}

class InviteUp {
    var timeOut: Int = 30// 30s超时
    var roomId: String = ""// 房间id
    var name: String = ""// 关键字
    var from: String = ""//专属房  记录从那申请的标识
    var leftText: String = ""
    var rightText: String = ""
    var canFree: Boolean = false
    var tag = ""
    var title = ""
    var image = ""
    var leftAction = ""
    var rightAction = ""
}

class LiveGift {
    var atId: Int = 0 // 收礼人id
    var atName = "" //收礼人名字

    //-2 属于充值专属服务
    var giftId = 0
    var giftImg = ""
    var giftName = ""
    var giftType = 0
    var giftEffect = ""
    var count: Int = 0
    var nickName: String = ""//送礼人名字
    var avatar: String = ""//送礼人头像,
    var gender = 0
    var userId = 0// 送礼人id
    var title = ""
    var type = false//等级进场特效
    var isPublisher = false
}

data class UserInfoChange(val roomRole: Int = 0,
                          val status: Int = 0  //拉黑操作状态（7 被拉黑，0 被取消拉黑）
)

data class UserLevel(val level: Int = 0)

data class SplitText(val text: String = "",
                     val color: String = "",
                     val action: String = "",
                     val fontScale: Float = 0.0f,
                     val deleteLine: Int = 0,
                     val underline: Int = 0,
                     val backgroundColor: String = "",
                     val style: Int = 0,//0,没有 1，加粗 2，斜体,3：加粗斜体
                     val verticalCenter: Boolean = false, //true 垂直居中，false 默认显示
                     val isImg: Int = 0,
                     var afterClickAction: String = ""// 如果有action且被用户点击后执行操作前先上报服务器这个字段
) : Serializable

data class RoomEnterAnim(val userId: Int = 0, val level: Int = 0, val text: String = "")

data class CouponInfo(val discountText: String = "",
                      val btnText: String = "",
                      val rightIcon: String = "",
                      val action: String = "",
                      val effect: String = "",
                      val effectBg: String = "",
                      val publisherId: Int = 0)

class SpecialServiceInfo(val serviceName: String = "",
                         val serviceDuration: String = "")

class EntryItemBean(val valStr: String = "", val desc: String = "")

// 优惠券消息
class CouponMessageItem(val couponId: Int = 0,
                        val request: String = "",
                        val user: UserInfo? = null,
                        val entryItems: List<EntryItemBean>? = null,
                        val rightButtonTipsColor: String = "",
                        val title: String = "",
                        val couponName: String = "",
                        val rightButtonTipsText: String = "")