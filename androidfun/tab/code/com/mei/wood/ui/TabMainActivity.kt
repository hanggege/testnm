package com.mei.wood.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.joker.im.custom.CustomType
import com.joker.im.imIsLogin
import com.joker.im.imLoginId
import com.joker.im.mapToConversation
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.agora.initAgora
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.SELECT_HOME
import com.mei.handleDeepLink
import com.mei.huawei.checkHuaweiPushKit
import com.mei.im.checkIMNewMessage
import com.mei.im.checkIMRefresh
import com.mei.im.ext.isCmdId
import com.mei.im.quickLoginIM
import com.mei.init.exitApp
import com.mei.init.meiApplication
import com.mei.live.action.registerSystemAction
import com.mei.live.action.requestCount
import com.mei.live.manager.refreshServiceMute
import com.mei.live.ui.fragment.requestEnterRoomConfig
import com.mei.live.ui.requestGiftInfo
import com.mei.login.checkLoginRegistered
import com.mei.message.SYSTEM_NOTIFY_USER_ID
import com.mei.orc.common.CommonConstant
import com.mei.orc.event.bindAction
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.runAsync
import com.mei.orc.ext.transparentStatusBar
import com.mei.orc.john.model.JohnUser
import com.mei.orc.rxpermission.requestMulPermission
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.orc.util.save.getValue
import com.mei.orc.util.save.putMMKV
import com.mei.orc.util.save.putValue
import com.mei.orc.util.string.getInt
import com.mei.push.checkPushStatus
import com.mei.wood.MainActionReceiver
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.constant.Preference
import com.mei.wood.dialog.checkAppUpgrade
import com.mei.wood.event.IMNewMsgEvent
import com.mei.wood.ext.Constants
import com.mei.wood.job.checkTestEnvironment
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.util.AppManager
import com.mei.wood.view.TabMenuView
import com.net.model.chick.tab.clearTabItem
import com.pili.player
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.*


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/9/1.
 */

class TabMainActivity : TabBaseActivity() {

    private val mainTabView: View by lazy { createView() }
    override val contentViews: List<ViewGroup> by lazy {
        contentViewIds.map {
            mainTabView.findViewById(it)
        }
    }
    override val tabMenuView: TabMenuView by lazy {
        mainTabView.findViewById(R.id.main_tab_bar)
    }

    private val imNewMsgEvent: IMNewMsgEvent by lazy { IMNewMsgEvent(this) }
    private val receiver: MainActionReceiver by lazy { MainActionReceiver(this) }

    override fun customStatusBar(): Boolean {
        return true
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainTabView)
        println("TabMainActivity :current:${System.currentTimeMillis()}")
        transparentStatusBar()
        lightMode()

        val intentFilter = IntentFilter()
        intentFilter.addAction(CommonConstant.Action.LOGIN())
        intentFilter.addAction(CommonConstant.Action.LOGOUT())
        intentFilter.addAction(CommonConstant.Action.SESSION_EXPIRED())
        registerReceiver(receiver, intentFilter)

        requestPermission()

        checkIMNewMessage { imNewMsgEvent.handleMessage(it) }
        checkIMRefresh { timUpdateUnreadLabel() }
        quickLoginIM(success = { timUpdateUnreadLabel() })
        initFragments()
        checkTestEnvironment(this)
        checkPushStatus()

        /** 初始化Agora与美颜，不放Application **/
        runAsync { initAgora() }
        bindAction<String>(Constants.RxBusTag.TabbarIndexSelected) { tag ->
            val index = tabItems.map { it.type.name.toLowerCase(Locale.getDefault()) }.indexOf(tag.orEmpty())
            setCurrentTabItem(index)
        }
        requestGiftInfo()
        requestEnterRoomConfig(true, back = {
            loadRoomConfig(it)
        })
        timUpdateUnreadLabel()

        bindAction<Boolean>(SELECT_HOME) {
            if (it == true) {
                setCurrentTabItem(0)
            }
        }
        bindAction<Boolean>(CHANG_LOGIN) {
            val oldIndex = getSelectedIndex()
            if (it == true) {
                requestCount = -1
            } else {
                player.stop()
            }
            clearTabItem()
            initFragments()
            setCurrentTabItem(oldIndex)

        }

        if ("isFirstCehckHuawei".getValue(true) == true) {
            /** 首次去检查华为厂商推送通道 **/
            checkHuaweiPushKit()
            "isFirstCehckHuawei".putValue(false)
        }

        showUnreadMessageDialog()
        checkImPushNotificationIntent(intent)
        meiApplication().registerSystemAction()

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkImPushNotificationIntent(intent)
    }


    override fun onResume() {
        super.onResume()
        checkAppUpgrade()
        refreshServiceMute()
    }

    override fun onBackPressed() {
        if (isOnDoubleClick(2000, "finish App")) {
            AppManager.getInstance().finishAllActivity()
        } else {
            UIToast.toast(this, this.getString(R.string.two_back_finish))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            unregisterReceiver(receiver)
            exitApp()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun rebuildPager(tabList: List<TabInfo>, index: Int) {
        super.rebuildPager(tabList, index)
        handleDeepLink()
    }

    override fun fragmentForTabItem(item: TabInfo): Fragment = findFragment(item)

    override fun onSelected(index: Int, old: Int) {
        super.onSelected(index, old)
        Preference.PREF_KEY_TAB_INDEX_NAME.putMMKV(tabItems.getOrNull(index)?.type?.name)
    }

    fun timUpdateUnreadLabel() {
        // 消息更新
        val index = tabItems.indexOfFirst { it.type == TAB_ITEM.MESSAGE }
        tabMenuView.itemViews.getIndexOrNull(index)?.let { itemView ->
            if (JohnUser.getSharedUser().hasLogin() && imIsLogin()) {
                TIMManager.getInstance().conversationList
                val conversationList = TIMManager.getInstance().conversationList
                        .filter {
                            it.type == TIMConversationType.C2C && !it.peer.toIntOrNull().isCmdId()
                        }
                        .filter { it.peer != imLoginId() }
                        .filter { getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true }
                val allCount = conversationList.sumBy {
                    /**产品刘紫茵说了，最后一条消息是拉黑的消息时不进行未读计数，但是如果最后一条消息之前有拉黑未读的消息需要进行计数**/
                    var count = 0
                    if (((it.mapToConversation().timConversation.lastMsg.mapToMeiMessage()
                                    as? CustomMessage)?.customMsgType
                                    == CustomType.exclusive_system_notify)
                            && !it.mapToConversation().timConversation.lastMsg.isRead) {
                        count = 1
                    }
                    it.unreadMessageNum.toInt() - count
                }
                val notifyCount = conversationList.filter { it.peer == SYSTEM_NOTIFY_USER_ID }.sumBy { it.unreadMessageNum.toInt() }
                itemView.messageNumber = when {
                    allCount == 0 -> 0
                    notifyCount == allCount -> -1
                    else -> allCount - notifyCount
                }
            } else {
                itemView.messageNumber = 0
            }
            ShortcutBadger.applyCount(this, itemView.messageNumber)
        }

    }

    /**
     * 权限申请
     */
    private fun requestPermission() {
        requestMulPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE)
    }

}