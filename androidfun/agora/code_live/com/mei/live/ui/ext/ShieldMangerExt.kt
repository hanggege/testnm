package com.mei.live.ui.ext

import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.common.SHIELD_USER
import com.mei.base.network.holder.SpiceHolder
import com.mei.dialog.showReportDialog
import com.mei.im.ui.popup.CommonPopupWindow
import com.mei.init.spiceHolder
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.string.getInt
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.chick.tab.isShowShieldingBtn
import com.net.model.room.RoomInfo
import com.net.model.user.DataCard
import com.net.network.chick.report.KeeperAddRequest
import com.net.network.chick.report.KeeperDeleteRequest
import com.net.network.chick.report.ShieldingAddRequest
import com.net.network.chick.report.ShieldingDeleteRequest

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.ext
 * @ClassName:      ShieldMangerExt
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/19 21:25
 * @UpdateUser:
 * @UpdateDate:     2020/6/19 21:25
 * @UpdateRemark:
 * @Version:
 */


/**
 * 设置为管理员
 */
private fun FragmentActivity.addKeeperExecuteKt(roomId: String, userId: String, back: (Int) -> Unit, dialogMiss: () -> Unit) {
    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    apiClient.executeKt(KeeperAddRequest(roomId, userId), success = {
        UIToast.toast(it.msg)
        dialogMiss()
    }, finish = {
        back(1)
    })
}

/**
 * 拉黑请求
 */
private fun FragmentActivity.shieldedExecuteKt(userId: String, roomId: String, back: (Int) -> Unit, dialogMiss: () -> Unit) {
    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    (this as? MeiCustomBarActivity)?.showLoading(true)
    apiClient.executeKt(ShieldingAddRequest(userId, roomId), success = {
        UIToast.toast(it.msg)
        if (it.isSuccess) {
            postAction(SHIELD_USER, if (it.data.userIds?.isNotEmpty() == true) it.data.userIds else arrayListOf(userId.getInt(0)))
            dialogMiss()
        }
    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = {
        back(1)
        (this as? MeiCustomBarActivity)?.showLoading(false)
    })
}

/**
 * 拉黑提示框
 */
fun FragmentActivity.shieldingDialog(tips: String, userId: String, roomId: String, back: (Int) -> Unit = {}, dialogMiss: () -> Unit = {}) {
    val viewRoot = layoutInflaterKt(R.layout.shielding_add_dialog)
    val dialog = NormalDialog().showDialog(this, DialogData(customView = viewRoot, canceledOnTouchOutside = false), back = null)
    viewRoot.findViewById<TextView>(R.id.cancel_dialog_btn)?.setOnClickListener {
        dialog.dismissAllowingStateLoss()
    }
    viewRoot.findViewById<TextView>(R.id.invite_title).text = tips
    viewRoot.findViewById<TextView>(R.id.confirm_dialog_btn)?.setOnClickListener {
        //确定拉黑
        dialog.dismissAllowingStateLoss()
        shieldedExecuteKt(userId, roomId, back, dialogMiss)
    }
}


/**
 * 取消黑名单
 */
private fun FragmentActivity.cancelDelete(userID: String, roomId: String, back: (Int) -> Unit, dialogMiss: () -> Unit) {
    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    apiClient.executeKt(ShieldingDeleteRequest(userID, roomId), success = {
        if (it.isSuccess) {
            postAction(CANCEL_SHIELD_USER, arrayListOf(userID.getInt(0)))
            dialogMiss()
        }
        UIToast.toast(it.msg)
    }, finish = {
        back(1)
    })
}

/**
 * 取消管理员设置
 */
private fun FragmentActivity.keeperDelete(userID: String, roomId: String, back: (Int) -> Unit, dialogMiss: () -> Unit) {
    val apiClient = (this as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
    apiClient.executeKt(KeeperDeleteRequest(roomId, userID), success = {
        UIToast.toast(it.msg)
        dialogMiss()
    }, finish = {
        back(1)
    })
}

/**
 * 主播端拉黑pop
 */
fun FragmentActivity.shieldingPop(dialog: DialogFragment, viewTarget: View, roomInfo: RoomInfo, userInfo: DataCard, back: (Int) -> Unit) {
    val popHeight = if ((roomInfo.isCreator || roomInfo.roomRole == 2)
            && roomInfo.isCommonRoom
            && getCacheUserInfo(userInfo.userId)?.isBlackList != true) dip(140) else dip(110)

    CommonPopupWindow.Builder(this).setView(R.layout.pop_shielding_layout)
            .setWidthAndHeight(dip(130), popHeight)
            .setViewOnclickListener { view, _ ->
                view.findViewById<TextView>(R.id.card_shielding).apply {
                    isVisible = roomInfo.roomRole > 0 || roomInfo.groupRights and 1 != 0 || roomInfo.groupRights and 8 != 0
                    (this@shieldingPop as? MeiCustomBarActivity)?.apiSpiceMgr?.requestImUserInfo(arrayOf(userInfo.userId), needRefresh = true, back = {
                        text = if (it.firstOrNull()?.isTheOtherToBlackList == true) "解除拉黑" else "拉黑"
                    })
                    setOnClickListener {
                        //拉黑
                        if (getCacheUserInfo(userInfo.userId)?.isTheOtherToBlackList == true) {
                            cancelDelete(userInfo.userId.toString(), roomInfo.roomId, back) {
                                (this@shieldingPop as? MeiCustomBarActivity)?.apiSpiceMgr?.requestImUserInfo(arrayOf(userInfo.userId), needRefresh = true, back = {
                                    dialog.dismissAllowingStateLoss()
                                })
                            }
                        } else {
                            shieldingDialog("是否确定拉黑 ${userInfo.nickname.orEmpty()}?", userInfo.userId.toString(), roomInfo.roomId, back) {
                                (this@shieldingPop as? MeiCustomBarActivity)?.apiSpiceMgr?.requestImUserInfo(arrayOf(userInfo.userId), needRefresh = true, back = {
                                    dialog.dismissAllowingStateLoss()
                                })
                            }
                            return@setOnClickListener
                        }
                    }
                }
                view.findViewById<TextView>(R.id.card_to_report).setOnClickListener {
                    //举报
                    showReportDialog(userInfo.userId, roomInfo.roomId, back)
                }
                view.findViewById<TextView>(R.id.card_set_manager).apply {
                    text = if (userInfo.roomRole == 0) "设为助教" else "取消助教"
                    isVisible = (roomInfo.isCreator || roomInfo.roomRole == 2) && roomInfo.isCommonRoom && getCacheUserInfo(userInfo.userId)?.isBlackList != true
                    setOnClickListener {
                        if (roomInfo.isCreator || roomInfo.roomRole == 2) {
                            if (userInfo.roomRole == 0) {
                                addKeeperExecuteKt(roomInfo.roomId, userInfo.userId.toString(), back) {
                                    dialog.dismissAllowingStateLoss()
                                }
                            } else {
                                keeperDelete(userInfo.userId.toString(), roomInfo.roomId, back) {
                                    dialog.dismissAllowingStateLoss()
                                }
                            }
                        }
                    }
                }
            }
            .setAnimationStyle(R.style.AnimDown).create().showAsDropDown(viewTarget, -dip(60), -dip(20))
}

enum class UserToMentorOptionsType {
    REPORT, //举报
    SHIELDING // 拉黑
}

/**
 * 用户端拉黑弹框
 */
fun FragmentActivity.showUserToShieldingDialog(roomInfo: RoomInfo, viewTarget: View, back: (String) -> Unit) {
    val view = layoutInflaterKt(R.layout.user_pop_shielding_layout)
    val popHeight = if (isShowShieldingBtn(MeiUser.getSharedUser().info?.userId != roomInfo.createUser?.userId)) dip(110) else dip(80)

    val pop = CommonPopupWindow.Builder(this).setView(view)
            .setOutsideTouchable(true)
            .setWidthAndHeight(dip(130), popHeight)
            .setAnimationStyle(R.style.AnimDown).create()
    pop.showAsDropDown(viewTarget, -dip(60), -dip(20))


    view.findViewById<View>(R.id.card_to_report).setOnClickListener {
        //举报
        back(UserToMentorOptionsType.REPORT.name)
        pop.dismiss()

    }
    view.findViewById<View>(R.id.card_shielding).apply {
        isVisible = isShowShieldingBtn(MeiUser.getSharedUser().info?.userId != roomInfo.createUser?.userId)
        setOnClickListener {
            //拉黑
            back(UserToMentorOptionsType.SHIELDING.name)
            pop.dismiss()
        }
    }
}

