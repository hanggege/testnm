package com.joker.im.custom.chick

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-04
 */
class InviteJoinInfo {
    var timeOut: Int = 0 // 30s超时
    var match: MatchInfo = MatchInfo()// 匹配的结果
    var matchMaker: MatchInfo = MatchInfo()// 匹配的结果
    var roomId: String = ""//房间id
    var groupId = ""
    var text = ""
    var leftGift = GiftGuideInfo()
    var rightGift = GiftGuideInfo()
}

class MatchInfo {
    var userName: String = ""
    var toUserId = 0
    var age: Int = 0
    var userAvatar: String = ""
    var gender: Int = 0
    var makeFriendHeart = ""
    var toUserInfoStr = "" //用户个人资料（与个人主页的资料一致，顺序也要一致）。个人资料最多展示5个标签。
}

class GiftGuideInfo {
    var giftId = 0
    var giftImage = ""
    var giftName = ""
    var giftType = 0
    var num = 0
    var text = ""
    var costCoin = 0
}