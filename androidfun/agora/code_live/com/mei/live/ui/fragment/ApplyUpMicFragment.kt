package com.mei.live.ui.fragment

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.round.RoundTextView
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeToastKt
import com.net.model.room.RoomApplyState
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomType
import com.net.model.room.RoomUserQueue.RoomUserQueueItem
import com.net.network.room.ApplyHandleRequest

/**
 * Created by hang on 2020-02-21.
 * 申请上麦队列
 */
class ApplyUpMicFragment : BaseUserListFragment() {

    override fun getListAdapter(list: MutableList<RoomUserQueueItem>): BaseQuickAdapter<RoomUserQueueItem, BaseViewHolder> {
        return ApplyUpMicAdapter(list).apply {
            this.back = callBack
        }
    }

    class ApplyUpMicAdapter(private val list: MutableList<RoomUserQueueItem>) :
            BaseQuickAdapter<RoomUserQueueItem, BaseViewHolder>(R.layout.item_apply_up_mic, list), LoadMoreModule {
        var back: () -> Unit = {}

        override fun convert(holder: BaseViewHolder, item: RoomUserQueueItem) {

            val activity = context as? VideoLiveRoomActivity

            val userAvatar = holder.getView<ImageView>(R.id.user_avatar)
            userAvatar.glideDisplay(item.user?.avatar.orEmpty(), item.user?.gender.genderAvatar(item.user.isPublisher))
            userAvatar.setOnClickListener {
                activity?.showUserInfoDialog(activity.roomInfo, item.user?.userId
                        ?: 0) { type, info ->
                    if (type == 0) {
                        activity.apiSpiceMgr.requestUserInfo(arrayOf(info.userId)) { list ->
                            activity.showInputKey(list.firstOrNull())
                        }
                    }
                }
            }

            val subTitle = buildSpannedString {
                item.subTitle.orEmpty().forEach {
                    appendLink(it.text, it.color.parseColor(Cxt.getColor(R.color.color_999999)))
                }
            }
            val title = buildSpannedString {
                appendLink(item.title?.text.orEmpty(), item.title?.color.parseColor(Cxt.getColor(R.color.color_333333)))
            }

            holder.setText(R.id.user_name, title)
                    .setVisible(R.id.free_apply, item.free)
                    .setVisible(R.id.label_apply_exclusive, RoomType.parseValue(item.roomType) == RoomType.EXCLUSIVE)
                    .setText(R.id.sub_title, subTitle)
                    .setVisible(R.id.sub_title, subTitle.isNotEmpty())
                    .setVisible(R.id.special_service_label, RoomType.parseValue(item.roomType) == RoomType.SPECIAL)
                    .setVisible(R.id.label_lack_of_balance_special, item.remainServiceDuration > 0)
                    .setText(R.id.label_lack_of_balance_special, "剩余${item.remainServiceDuration}分钟")
                    .setVisible(R.id.label_lack_of_balance, item.lessThanFiveMin)
            val userName = holder.getView<TextView>(R.id.user_name)
            userName.paint.isFakeBoldText = true


            val agreeUpMic = holder.getView<RoundTextView>(R.id.agree_up_mic)
            if (item.state == RoomApplyState.WAITE_USER || item.state == RoomApplyState.WAITE_USER_REPLY) {
                agreeUpMic.isSelected = false
                agreeUpMic.text = "等待响应"
                agreeUpMic.setTextColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
                agreeUpMic.setStrokeColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
            } else if (item.state == RoomApplyState.APPLY) {
                agreeUpMic.isSelected = true
                agreeUpMic.text = "同意连线"
                agreeUpMic.setTextColor(ContextCompat.getColor(context, R.color.color_ff8200))
                agreeUpMic.setStrokeColor(ContextCompat.getColor(context, R.color.color_ff8200))
            }
            val hasWaiting = list.any {
                it.state == RoomApplyState.WAITE_USER || it.state == RoomApplyState.WAITE_USER_REPLY
            }
            agreeUpMic.setOnClickNotDoubleListener {
                if (agreeUpMic.isSelected) {
                    when {
                        context.upstreamUserIds().size == 2 -> UIToast.toast("麦上已有用户,请稍后操作")
                        hasWaiting -> UIToast.toast("等待上一位用户连麦响应")
                        else -> {
                            val request = ApplyHandleRequest().apply {
                                this.userId = item.user?.userId ?: 0
                                this.roomId = activity?.roomId.orEmpty()
                                this.type = RoomApplyType.UPSTREAM
                                this.result = 1
                            }
                            activity?.apiSpiceMgr?.executeToastKt(request, success = {
                                if (it.isSuccess) {
                                    back()
                                }
                            })
                        }
                    }
                }
            }
        }

    }
}
