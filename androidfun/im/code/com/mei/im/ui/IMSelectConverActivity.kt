package com.mei.im.ui

import android.os.Bundle
import android.os.Parcelable
import android.text.TextUtils
import com.joker.im.Conversation
import com.joker.im.imIsLogin
import com.joker.im.imLoginId
import com.joker.im.mapToConversation
import com.mei.base.weight.recyclerview.manager.WrapLayoutManager
import com.mei.im.adapter.IMSelectConverAdapter
import com.mei.im.quickLoginIM
import com.mei.orc.ActivityLauncher
import com.mei.orc.common.CommonConstant
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.ui.MeiCustomBarActivity
import com.tencent.imsdk.TIMConversation
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import kotlinx.android.synthetic.main.activity_imselect_conver.*
import launcher.Boom
import java.util.*

class IMSelectConverActivity : MeiCustomBarActivity() {

    val adapterList: ArrayList<Conversation> = ArrayList()
    var converAdapter: IMSelectConverAdapter = IMSelectConverAdapter(adapterList)

    @Boom
    var shareLink: Parcelable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_imselect_conver)
        ActivityLauncher.bind(this)
        if (imIsLogin()) {
            initData()
        } else if (JohnUser.getSharedUser().hasLogin()) {
            showLoading(true)
            quickLoginIM(success = {
                initData()
            }, failure = { _, msg ->
                UIToast.toast(this, msg)
            })
        } else {
            UIToast.toast(this, "登录信息失效，请重新登录")
            finish()
        }
    }

    private fun initData() {
        adapterList.addAll(TIMManager.getInstance().conversationList
                .filter { it.peer != imLoginId() && checkCanShow(it) }
                .map { it.mapToConversation() })
        select_conversation_rc.layoutManager = WrapLayoutManager(this)
        select_conversation_rc.adapter = converAdapter
        converAdapter.setOnItemClickListener { _, _, position ->
            val conversation: Conversation? = converAdapter.getItemOrNull(position)
            if (conversation != null && conversation.peer != CommonConstant.IMOfficialMelkor.SYS_USER_ID_GROUPNOTIFY) {//不是群通知
                //TODO 没有用到暂时注释掉
//                this@IMSelectConverActivity.toImChat(conversation.peer, conversation.type == TIMConversationType.Group, shareLink)
                finish()
            }
        }
        /** 预加载用户信息 **/
        apiSpiceMgr.requestUserInfo(adapterList.filter { it.type == TIMConversationType.C2C }.mapNotNull { it.peer.toIntOrNull() }.toTypedArray(), back = {
            converAdapter.notifyDataSetChanged()
        })
        converAdapter.notifyDataSetChanged()

    }

    private fun checkCanShow(it: TIMConversation): Boolean {

        return it.type != TIMConversationType.System
                && it.type != TIMConversationType.Group
                && !TextUtils.isEmpty(it.peer)
                && !TextUtils.equals(it.peer, CommonConstant.IMOfficialMelkor.SYS_USER_ID_PAY_TIP)//  小鹿情感支付
                && !TextUtils.equals(it.peer, CommonConstant.IMOfficialWood.ID_XIAOLU_OFFICIAL)
//                && !IMConversationHeader.STICK_IDS_STR.contains(it.peer)
    }

}
