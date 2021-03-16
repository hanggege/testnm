package com.mei.live.ui.dialog

import android.os.Handler
import android.os.Message
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.LiveGift
import com.joker.im.custom.chick.SplitText
import com.mei.dialog.myRose
import com.mei.dialog.roseFromSceneEnum2PayFromType
import com.mei.dialog.showPayDialog
import com.mei.live.manager.liveSendCustomMsg
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.views.ANIMATOR_DURATION
import com.mei.orc.Cxt
import com.mei.orc.ext.letElse
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager
import com.net.MeiUser
import com.net.model.gift.GiftInfo
import com.net.model.rose.RoseFromSceneEnum
import com.net.network.gift.SendGiftRequest

/**
 * Created by hang on 2019-12-17.
 */

/**
 * 请求送礼接口
 */
fun MeiCustomActivity.requestSendGift(
        room_id: String,
        group_id: String,
        at_id: Int,
        at_name: String,
        gift_count: Int,
        giftInfo: GiftInfo,
        roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.PERSONAL_PAGE,
        fromType: String = "用户主动送礼",
        dialogBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> },
        statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }
) {
    if (MeiUser.getSharedUser().extra?.coinBalance?.compareTo(giftInfo.costCoin * gift_count) ?: 0 < 0) {
        /**首先判断当前余额是否充足*/
        showPayDialog(roseFromScene.roseFromSceneEnum2PayFromType()) { success ->
            if (success) {
                myRose { balance ->
                    dialogBack(false, balance)
                }
            }
        }
        UIToast.toast("当前心币数量不足,请充值")
        liveSendCustomMsg(room_id, CustomType.send_text, applyData = {
            whitelist.add(at_id)
            val name = MeiUser.getSharedUser().info?.nickname.orEmpty()
            content = arrayListOf<SplitText>().apply {
                if (name.isNotEmpty()) add(SplitText(name, "#B3FFFFFF"))
                add(SplitText(" 因余额不足，送礼物不成功"))
            }
        })

    } else {
        /**先扣钱 送礼失败再还原*/
        val balance = MeiUser.getSharedUser().extra?.coinBalance?.minus(giftInfo.costCoin.toDouble() * gift_count)
                ?: 0.00
        MeiUser.getSharedUser().extra?.coinBalance = balance

        dialogBack(false, balance)
        /**banner动画*/
        (activity as? VideoLiveRoomActivity)?.autoStartBannerAnim(giftInfo, at_name, at_id, gift_count)
        sendMsg(room_id, group_id, at_id, at_name, gift_count, giftInfo, roseFromScene, fromType, dialogBack, statuBack)
    }

}

/**消息列表，记录连击的礼物信息*/
private val giftMsgList = arrayListOf<GiftMsg>()

/**
 * 消息面板消息需要等待连击完成之后发送
 */
private fun MeiCustomActivity.sendMsg(
        room_id: String,
        group_id: String,
        at_id: Int,
        at_name: String,
        gift_count: Int,
        giftInfo: GiftInfo,
        roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.PERSONAL_PAGE,
        fromType: String = "用户主动送礼",
        dialogBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> },
        statuBack: (type: Int, gift: LiveGift?) -> Unit = { _, _ -> }
) {
    if (mBaseHandler.hasMessages(100)) {
        if (giftMsgList.firstOrNull()?.gift_id != giftInfo.giftId || giftMsgList.firstOrNull()?.at_id != at_id) {
            startSend(roseFromScene, fromType, dialogBack, statuBack)
        }
        mBaseHandler.removeMessages(100)
    }
    giftMsgList.add(
            GiftMsg(
                    room_id,
                    group_id,
                    at_name,
                    at_id,
                    giftInfo.giftId,
                    giftInfo.giftName.orEmpty(),
                    gift_count,
                    giftInfo.giftType,
                    giftInfo.bannerImage.orEmpty(),
                    giftInfo.costCoin
            )
    )
    val runnable = Runnable { (AppManager.getInstance().currentActivity() as? MeiCustomActivity)?.startSend(roseFromScene, fromType, dialogBack, statuBack) }
    mBaseHandler.sendMessageDelayed(Message.obtain().apply {
        what = 100
        obj = runnable
    }, if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) 0 else ANIMATOR_DURATION)
}


private val mBaseHandler: Handler = object : Handler(Cxt.get().mainLooper) {
    override fun handleMessage(msg: Message) {
        when (msg.what) {
            100 -> {
                post(msg.obj as Runnable)
            }
        }
    }
}

private data class GiftMsg(
        val room_id: String,
        val group_id: String,
        val at_name: String,
        val at_id: Int,
        val gift_id: Int,
        val gift_name: String,
        val gift_count: Int,
        val gift_type: Int,
        val gift_img: String,
        val costCoin: Int
)

private fun MeiCustomActivity.startSend(roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.PERSONAL_PAGE, fromType: String = "用户主动送礼", dialogBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> }, statuBack: (type: Int, gift: LiveGift?) -> Unit) {
    giftMsgList.firstOrNull().letElse(nonullBack = {
        val giftCount = giftMsgList.sumBy { it.gift_count }
        startRealSendGift(it, giftCount, roseFromScene, fromType, dialogBack, statuBack)
    })
    giftMsgList.clear()
}


/**
 * 没有连击之后，真正的开始请求送礼接口
 */
private fun MeiCustomActivity.startRealSendGift(
        giftMsg: GiftMsg, gift_count: Int, roseFromScene: RoseFromSceneEnum = RoseFromSceneEnum.COMMON_ROOM, fromType: String = "用户主动送礼",
        dialogBack: (isHide: Boolean, balance: Double) -> Unit = { _, _ -> },
        statuBack: (type: Int, gift: LiveGift?) -> Unit
) {

    /**开始请求送礼*/
    com.mei.init.spiceHolder().apiSpiceMgr.executeToastKt(SendGiftRequest().apply {
        giftId = giftMsg.gift_id
        number = gift_count
        toUserId = giftMsg.at_id
        fromScene = roseFromScene.name
        referId = giftMsg.room_id
        this.fromType = fromType
    }, success = {
        if (it.isSuccess) {
            statuBack(0, LiveGift().apply {
                count = gift_count
                giftImg = giftMsg.gift_img
                giftName = giftMsg.gift_name
            })
            it.data?.let { data ->
                MeiUser.getSharedUser().extra?.coinBalance = data.balance
                dialogBack(false, data.balance)

            }
        }
    }, failure = {
        val balance = MeiUser.getSharedUser().extra?.coinBalance?.plus((giftMsg.costCoin * gift_count).toDouble())
                ?: 0.00
        MeiUser.getSharedUser().extra?.coinBalance = balance
        dialogBack(false, balance)
    }, finish = {
        if (roseFromScene == RoseFromSceneEnum.EXCLUSIVE_CHAT) {
            (AppManager.getInstance().currentActivity() as? MeiCustomActivity)?.showLoading(false)
        }
    })
}
