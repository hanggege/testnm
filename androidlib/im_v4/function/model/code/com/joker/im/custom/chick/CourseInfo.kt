package com.joker.im.custom.chick

import java.io.Serializable

/**
 *
 * @author Created by lenna on 2020/7/6
 */
class CourseInfo : Serializable {
    /**课程id*/
    var courseId = 0

    /**主播id*/
    var userId = 0

    /**课程名称*/
    var courseName = ""

    /**课程售价（单位砖）*/
    var cost = 0

    /**
     * 售价富文本
     */
    var priceText: List<SplitText> = arrayListOf()

    /**原价（单位砖）*/
    var crossedPrice = 0

    /**总讲数*/
    var audioCount = 0

    /**是否可用*/
    var inUse: Boolean = false

    /**是否已购买*/
    var hasBuy: Boolean = false

    /**免费领取*/
    var hasReceive: Boolean = false

    /**
     * 是否是自己
     */
    var isSelf: Boolean = false

    var cardType = CardType.TYPE_NORMAL

    var backgroundColor = ""

    // 是否有付费的音频
    var hasFreeAudition: Boolean = false
    var isGroupMember: Boolean = false

    // 课程当前工作室roomId
    var roomId: String? = null

    // 课程当前工作室groupId
    var groupId: Int = 0

    object CardType {
        const val TYPE_NORMAL = "normal"
        const val TYPE_CANCEL = "cancel"
        const val TYPE_RECOMMEND = "recommend"
        const val TYPE_RECHARGE = "recharge"
        const val TYPE_ASK = "ask"
    }


}