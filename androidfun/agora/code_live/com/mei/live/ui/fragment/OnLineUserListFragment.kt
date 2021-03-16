package com.mei.live.ui.fragment

import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.live.ext.parseColor
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.showSelectServiceDialog
import com.mei.live.ui.dialog.showUserInfoDialog
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.widget.round.RoundTextView
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.appendLink
import com.net.model.room.RoomApplyState
import com.net.model.room.RoomApplyType
import com.net.model.room.RoomType
import com.net.model.room.RoomUserQueue.InviteOption
import com.net.model.room.RoomUserQueue.RoomUserQueueItem
import com.net.model.room.RoomUserTypeEnum


/**
 * Created by hang on 2020-02-21.
 */
class OnLineUserListFragment : BaseUserListFragment() {

    var applyOption: InviteOption? = null

    override fun getListAdapter(list: MutableList<RoomUserQueueItem>): BaseQuickAdapter<RoomUserQueueItem, BaseViewHolder> {
        return OnlineUserAdapter(list)
    }

    inner class OnlineUserAdapter(private val list: MutableList<RoomUserQueueItem>) :
            BaseQuickAdapter<RoomUserQueueItem, BaseViewHolder>(R.layout.item_online_user, list),
            LoadMoreModule {

        val activity by lazy { context as VideoLiveRoomActivity }
        override fun convert(holder: BaseViewHolder, item: RoomUserQueueItem) {
            val userAvatar = holder.getView<ImageView>(R.id.user_avatar)
            userAvatar.glideDisplay(item.user?.avatar.orEmpty(), item.user?.gender.genderAvatar(item.user?.isPublisher))
            userAvatar.setOnClickListener {
                activity.showUserInfoDialog(activity.roomInfo, item.user?.userId
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
                    .setText(R.id.sub_title, subTitle)
                    .setVisible(R.id.sub_title, item.subTitle.orEmpty().isNotEmpty())

            holder.getView<TextView>(R.id.service_name).apply {
                val mySelf = if (activity.roomInfo.isStudio) {
                    item.medalMap.orEmpty()[activity.roomInfo.groupInfo?.groupId.toString()]
                } else {
                    item.medalMap.orEmpty()[JohnUser.getSharedUser().userIDAsString]
                }
                isVisible = mySelf != null
                text = mySelf?.medal.orEmpty()
            }

            val userName = holder.getView<TextView>(R.id.user_name)
            userName.paint.isFakeBoldText = true

            val inviteUpstream = holder.getView<RoundTextView>(R.id.invite_upstream)
            inviteUpstream.isVisible = !item.btnText.isNullOrEmpty()

            if (item.state == RoomApplyState.WAITE_USER || item.state == RoomApplyState.WAITE_USER_REPLY) {
                inviteUpstream.isSelected = false
                inviteUpstream.text = "等待响应"
                inviteUpstream.setTextColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
                inviteUpstream.setStrokeColor(ContextCompat.getColor(context, R.color.color_c8c8c8))
            } else {
                inviteUpstream.isSelected = true
                inviteUpstream.text = item.btnText ?: "邀请连线"
                inviteUpstream.setTextColor(ContextCompat.getColor(context, R.color.color_ff8200))
                inviteUpstream.setStrokeColor(ContextCompat.getColor(context, R.color.color_ff8200))
            }

            fun onClickAction() {
                when (roomUserType) {
                    RoomUserTypeEnum.ROOM_USER, RoomUserTypeEnum.ROOM_OLD_USER -> {
                        activity.inviteUpStream(activity.roomId, item.user?.userId
                                ?: 0, RoomApplyType.parseValue(applyOption?.applyType.orEmpty()),
                                RoomType.parseValue(applyOption?.roomType), applyOption?.costTips.orEmpty()) {
                            item.state = RoomApplyState.WAITE_USER
                            notifyItemChanged(holder.bindingAdapterPosition)
                        }
                    }
                    RoomUserTypeEnum.ROOM_SPECIAL_USER -> {
                        applyOption?.let {
                            activity.showSelectServiceDialog(this@OnLineUserListFragment, item.user?.userId
                                    ?: 0, it) {
                                item.state = RoomApplyState.WAITE_USER
                                notifyItemChanged(holder.bindingAdapterPosition)
                            }
                        }
                    }
                    else -> {
                    }
                }
            }

            inviteUpstream.setOnClickNotDoubleListener {
                if (inviteUpstream.isSelected) {
                    onClickAction()
                } else {
                    UIToast.toast("等待用户连麦响应")
                }
            }


        }

    }


}