package com.mei.live.ui.dialog

import android.Manifest
import android.os.Bundle
import android.view.View
import com.mei.GrowingUtil
import com.mei.chat.ui.view.requestExclusiveMatchApply
import com.mei.live.ui.dialog.adapter.QuickMatchFieldAdapter
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.permission.PermissionCheck
import com.mei.wood.R
import com.mei.wood.dialog.DISSMISS_DO_OK
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.room.MatchInfo
import com.net.network.room.QuickMatchRequest
import kotlinx.android.synthetic.main.dialog_matching.*

/**
 *
 * @author Created by lenna on 2020/11/16
 * @param couponNum 使用优惠券 但是不指定使用那一张则传-1, 具体使用那一张则传具体优惠券的编号. 0 表示不使用优惠券
 * 快速咨询匹配知心达人窗口
 */
fun MeiCustomActivity.showMatchingDialog(couponNum: Long, proCateId: Int, userType: String) {
    MatchingDialog().apply {
        this.couponNum = couponNum
        this.proCateId = proCateId
        this.userType = userType
        show(supportFragmentManager, "MatchingDialog")
    }
    statBrowse(userType)


}

/**浏览统计*/
fun statBrowse(status: String) = try {
    GrowingUtil.track("function_view",
            "function_name", "快速咨询",
            "status", status,
            "function_page", "选择匹配知心人页")
} catch (e: Exception) {
    e.printStackTrace()
}


fun statClick(status: String, clickType: String) = try {
    GrowingUtil.track("function_click",
            "function_name", "快速咨询",
            "status", status,
            "function_page", "选择匹配知心人页",
            "click_type", clickType)
} catch (e: Exception) {
    e.printStackTrace()
}

class MatchingDialog : MeiSupportDialogFragment() {
    var couponNum: Long = 0
    var proCateId: Int = 0
    var userType: String = ""
    var matchInfo: MatchInfo? = null
    private val fieldList = arrayListOf<MatchInfo.PublisherSkillsBean>()
    private val fieldAdapter by lazy {
        QuickMatchFieldAdapter(fieldList)
    }

    override fun onSetInflaterLayout() = R.layout.dialog_matching
    override fun isFullScreen() = true
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        (activity as? MeiCustomBarActivity)?.showLoading(true)
        (activity as? MeiCustomActivity)?.apiSpiceMgr?.executeKt(QuickMatchRequest(proCateId), success = {
            if (it.isSuccess) {
                matchInfo = it.data
                refreshData()
                UIToast.toast("已为您匹配成功")
            } else {
                UIToast.toast(it.errMsg)
            }
        }, failure = {
            UIToast.toast(it?.currMessage)
        }, finish = {
            (activity as? MeiCustomBarActivity)?.showLoading(false)
        })
    }

    private fun initView() {
        reset_matching.setOnClickListener {
            resetMatch()
            statClick(userType, reset_matching.text.toString())
        }
        close_matching_iv.setOnClickListener {
            dismiss()
        }
        immediately_attachment.setOnClickNotDoubleListener(tag = "attachment") {

            //立即连线
            activity?.requestMulPermissionZip(Manifest.permission.RECORD_AUDIO, Manifest.permission.CAMERA) {
                // 两个权限都需要才能连线
                if (it) {
                    (activity as? MeiCustomActivity)?.requestExclusiveMatchApply(matchInfo?.publisherUserId
                            ?: 0, couponNum = couponNum, categoryId = proCateId, userType = userType)
                    dismiss()
                } else {
                    NormalDialogLauncher.newInstance().showDialog(activity, "用户连线需要摄像头和录音权限，请打开相应权限", back = { result ->
                        if (result == DISSMISS_DO_OK) {
                            PermissionCheck.gotoPermissionSetting(activity)
                        }
                    })
                }
            }

            statClick(userType, immediately_attachment.text.toString())
        }
        matching_user_tag_rv.adapter = fieldAdapter

    }

    private fun refreshData() {
        if (isAdded) {
            matching_user_avatar.glideDisplay(matchInfo?.avatar.orEmpty())
            matching_user_name.apply {
                paint.isFakeBoldText = true
                text = matchInfo?.name.orEmpty()
            }
            fieldList.clear()
            fieldList.addAll(matchInfo?.publisherSkillList ?: arrayListOf())
            fieldAdapter.notifyDataSetChanged()
        }


    }

    private fun resetMatch() {
        (activity as? MeiCustomBarActivity)?.showLoading(true)
        (activity as? MeiCustomBarActivity)?.apiSpiceMgr?.executeKt(QuickMatchRequest(proCateId), success = {
            if (it.isSuccess) {
                matchInfo = it.data
                refreshData()
                if (it.data.onlyOne) {
                    UIToast.toast("没有更多了")
                }
            } else {
                UIToast.toast(it.errMsg)
            }
        }, failure = {
            UIToast.toast(it?.currMessage)
        }, finish = {
            (activity as? MeiCustomBarActivity)?.showLoading(false)
        })
    }
}