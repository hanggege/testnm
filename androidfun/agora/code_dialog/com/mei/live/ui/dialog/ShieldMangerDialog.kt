package com.mei.live.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.agora.event.AgoraEventHandler
import com.mei.base.ui.nav.Nav
import com.mei.dialog.PayFromType
import com.mei.dialog.myRose
import com.mei.dialog.showPayDialog
import com.mei.im.ui.view.menu.ChatMenu
import com.mei.live.ui.fragment.appendRoomHalfScreenParamsUrl
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomBottomDialogFragment
import com.mei.wood.util.MeiUtil
import com.net.network.share.LivingShareRequest
import kotlinx.android.synthetic.main.shield_manager_dialog.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog
 * @ClassName:      ShieldMangerDialog
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/16 14:07
 * @UpdateUser:
 * @UpdateDate:     2020/6/16 14:07
 * @UpdateRemark:
 * @Version:
 */
//黑名单管理
fun FragmentActivity.showShieldMangerDialog(roomId: String, menuList: List<ChatMenu>): ShieldMangerDialog {
    val shieldMangerDialog = ShieldMangerDialog().apply {
        this.roomId = roomId
        this.menuList = menuList
    }
    shieldMangerDialog.show(supportFragmentManager, "ShieldMangerDialog")
    return shieldMangerDialog
}

class ShieldMangerDialog : CustomBottomDialogFragment(), AgoraEventHandler {
    var roomId = ""
    var menuList: List<ChatMenu> = arrayListOf()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.shield_manager_dialog, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        menu_list.adapter = object : BaseQuickAdapter<ChatMenu, BaseViewHolder>(R.layout.item_shield_manger) {

            override fun convert(holder: BaseViewHolder, item: ChatMenu) {
                val itemText = holder.getView<TextView>(R.id.item_text)
                val itemIcon = holder.getView<ImageView>(R.id.item_icon)

                itemText.text = item.menuName
                itemIcon.glideDisplay(item.menuIcon, R.drawable.icon_keeper)

                holder.itemView.setOnClickListener {
                    when (item.menuType) {
                        ChatMenu.MenuType.TYPE_SEND_TEST -> {
                            dismissAllowingStateLoss()
                            val url = MeiUtil.appendParamsUrl(item.actionUrl, Pair("room_id", roomId))
                            activity?.showExclusiveServiceDialog(activity?.appendRoomHalfScreenParamsUrl(url).orEmpty(), roomId)
                        }
                        ChatMenu.MenuType.TYPE_SHIELD_MANAGER -> {
                            dismissAllowingStateLoss()
                            activity?.showShieldMangerFragmentDialog(roomId, 0)
                        }
                        ChatMenu.MenuType.TYPE_KEEPER_MANAGER -> {
                            dismissAllowingStateLoss()
                            activity?.showShieldMangerFragmentDialog(roomId, 1)
                        }
                        ChatMenu.MenuType.TYPE_RECHARGE -> {
                            activity?.showPayDialog(PayFromType.BROADCAST) {
                                if (it && isAdded) {
                                    activity.myRose()
                                }
                            }
                        }
                        ChatMenu.MenuType.TYPE_SHARE_LIVE -> {
                            apiSpiceMgr.executeKt(LivingShareRequest(roomId), success = { rep ->
                                rep?.data?.let {
                                    dismissAllowingStateLoss()
                                    activity?.showLivingShareDialog(it)
                                }
                            })
                        }
                        ChatMenu.MenuType.TYPE_SUGGESTION -> {
                            dismissAllowingStateLoss()
                            Nav.toAmanLink(activity, item.actionUrl)
                        }
                    }
                }
            }
        }.apply {
            setList(menuList)
        }
    }

}