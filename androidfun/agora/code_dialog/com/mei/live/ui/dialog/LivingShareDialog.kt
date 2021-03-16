package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.dialog.share.ShareData
import com.mei.wood.dialog.share.ShareManager
import com.mei.wood.dialog.share.wechat
import com.mei.wood.dialog.share.wechat_circle
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.appendLink
import com.mei.wood.util.MeiUtil
import com.net.model.room.RoomInfo
import com.net.model.share.LivingShareResult
import kotlinx.android.synthetic.main.dialog_living_share.*

/**
 *  Created by lh on 2019-12-7
 *  Des: 分享
 */

fun FragmentActivity.showLivingShareDialog(
        livingShareResult: LivingShareResult,
        fromType: String = "用户主动分享",
        callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }
) {
    if (supportFragmentManager.findFragmentByTag("showLivingShareDialog") == null) {
        LivingShareDialog(livingShareResult).apply {
            this.callBack = callBack
            this.fromType = fromType
        }.show(supportFragmentManager, "showLivingShareDialog")
    }
}


class LivingShareDialog(private val livingShareResult: LivingShareResult) : BottomDialogFragment() {
    var roomId = ""
    private var shareData: ShareData = ShareData()
    var callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }
    var fromType: String = "用户主动分享"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_living_share, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        livingShareResult.let {
            GrowingUtil.track(
                    "invite_broadcast_popup_view",
                    "role", if (it.isPublisher) "主播" else "普通用户"

            )
            share_dialog_detail.isVisible = it.activity
            share_dialog_num.isVisible = it.activity
            share_dialog_detail.text = it.activityPageButton
            share_dialog_num.text = if (it.isPublisher) {
                buildSpannedString {
                    append(it.firstText ?: "")
                    appendLink(" ${it.inviteUserParam?.userCountPublisher} ", Color.parseColor("#ffffd638"), 1.8f)
                    appendLink("${it.theUnit ?: ""}\n", Color.parseColor("#ffffd638"), 1f)
                    append(it.publisherText)
                    appendLink(" ${it.cashBack}% ", Color.parseColor("#ffffd638"), 1.8f)
                    appendLink(it.publisherUnit ?: "", Color.parseColor("#ffffd638"), 1f)
                }
            } else {
                buildSpannedString {
                    append(it.firstText ?: "")
                    appendLink(" ${it.inviteUserParam?.userCount} ", Color.parseColor("#ffffd638"), 1.8f)
                    appendLink(it.theUnit ?: "", Color.parseColor("#ffffd638"), 1f)
                }
            }
            shareData = ShareData(it.shareTitle.orEmpty(), it.shareSubject.orEmpty(), it.shareImage.orEmpty(), MeiUtil.appendParamsUrl(MeiUtil.appendShareUrl(AmanLink.NetUrl.living_invite_url), "roomId" to roomId, "inviteId" to JohnUser.getSharedUser().getUserID().toString()))
        }




        share_dialog_detail.setOnClickListener {
            //详情
            Nav.toWebActivity(activity, MeiUtil.appendParamsUrl(MeiUtil.appendShareUrl(livingShareResult.activityUrl), "roomId" to roomId))

        }

        share_dialog_close.setOnClickListener {
            dismissAllowingStateLoss()
        }

        share_dialog_wechat.setOnClickListener {
            GrowingUtil.track(
                    "invite_broadcast_popup_click",
                    "role", if (livingShareResult.isPublisher) "主播" else "普通用户",
                    "click_type", "邀请微信好友"

            )
            activity?.let {
                ShareManager.instance.handleShareChoice(
                        it,
                        wechat,
                        shareData
                )
            }
            dismissAllowingStateLoss()
        }

        share_dialog_public.setOnClickListener {
            GrowingUtil.track(
                    "invite_broadcast_popup_click",
                    "role", if (livingShareResult.isPublisher) "主播" else "普通用户",
                    "click_type", "分享至朋友圈"

            )
            activity?.let {
                ShareManager.instance.handleShareChoice(
                        it,
                        wechat_circle,
                        shareData
                )
            }
            dismissAllowingStateLoss()
        }

    }

    override fun getBackgroundAlpha(): Float {
        return 0f
    }
}