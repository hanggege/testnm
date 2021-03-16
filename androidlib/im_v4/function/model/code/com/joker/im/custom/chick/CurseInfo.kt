package com.joker.im.custom.chick

import java.io.Serializable

/**

* @Package:        com.joker.im.custom
 * @ClassName:      ServiceAdInfo
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/5/27 18:10
 * @UpdateUser:
 * @UpdateDate:     2020/5/27 18:10
 * @UpdateRemark:
 * @Version:
 */
class CurseInfo : Serializable {
    var cardType: String = CardType.TYPE_NORMAL //normal 正常的，取消 cancel
    var serviceName = ""
    var serviceMinutes = 0//单次服务时长
    var serviceCost = 0//砖石
    var serviceMin = 0//起步次数
    var publisherId = 0//知心达人ID
    var specialServiceId = 0//服务ID
    var serviceContent = ""//公屏内容
    var timeRemaining = 0  // 剩余时长
    var upCount = 0        // 连线次数
    var serviceContext = ""//服务操作公屏
    var serviceIntroduction = "" //服务介绍
    var specialServiceOrderId = "" //订单号
    var userId = 0//购买用户id
    var isSelf = false

    object CardType {
        const val TYPE_NORMAL = "normal"
        const val TYPE_CANCEL = "cancel"
        const val TYPE_RECOMMEND = "recommend"
        const val TYPE_RECHARGE = "recharge"
    }
}

