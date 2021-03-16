@file:Suppress("DEPRECATION")

package com.mei.wood.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils
import android.webkit.CookieManager
import android.webkit.CookieSyncManager
import androidx.fragment.app.Fragment
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.imLogout
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.common.REFRESH_HOME_DATA
import com.mei.base.ui.nav.Nav
import com.mei.chat.toImChat
import com.mei.find.ui.FindTabFragment
import com.mei.im.PUSH_MESSAGE_IDENTIFY
import com.mei.im.PUSH_MESSAGE_IS_GROUP
import com.mei.im.PUSH_TIM_REPORT_DATA
import com.mei.im.resetNotifyAll
import com.mei.init.requestTabBar
import com.mei.intimate.IntimateHomeFragment
import com.mei.login.checkLoginRegistered
import com.mei.me.MineTabFragment
import com.mei.message.MessageFragment
import com.mei.orc.event.postAction
import com.mei.orc.ext.inList
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.save.getObjectMMKV
import com.mei.orc.util.save.getStringMMKV
import com.mei.short_video.ShortVideoTabFragment
import com.mei.wood.R
import com.mei.wood.constant.Preference
import com.mei.wood.event.applyExclusive
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.modle.TAB_ITEM
import com.mei.wood.ui.fragment.TabItemFragment
import com.mei.wood.util.logDebug
import com.mei.wood.util.parseValue
import com.net.model.chick.tab.TabItem
import com.net.model.chick.tab.tabbarConfig
import com.net.model.room.GenericEffectConfig
import com.net.network.chat.MessageUnreadRequest
import com.net.network.chick.friends.HomeStatusRequest
import com.net.network.report.MessagePushReport
import com.opensource.svgaplayer.SVGAParser
import java.net.URL

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/9/1.
 */

fun TabMainActivity.initFragments() {
    val tabarInfo = tabbarConfig.tabs
    val tabbarList: ArrayList<TabItem> = arrayListOf()

    tabbarList.addAll(tabarInfo)
    //如果没有缓存就取一下
    if (tabbarList.isEmpty()) {
        showLoadingCover()
        requestTabBar {
            if (it?.tabs.orEmpty().isEmpty()) {
                tabbarList.add(TabItem(TAB_ITEM.HOME.name, "直播"))
                tabbarList.add(TabItem(TAB_ITEM.SHORT_VIDEO.name, "短视频"))
                tabbarList.add(TabItem(TAB_ITEM.DISCOVERY.name, "发现"))
                tabbarList.add(TabItem(TAB_ITEM.MESSAGE.name, "消息"))
                tabbarList.add(TabItem(TAB_ITEM.MY.name, "我的"))
            } else {
                //这里肯定有数据的
                tabbarList.addAll(it?.tabs.orEmpty())
            }
            showLoading(false)
            tabItem2TabInfo(tabbarList)
            if (TAB_ITEM.MESSAGE.name == it?.defaultTab) { //新用户跳转消息页后要刷新首页
                postAction(REFRESH_HOME_DATA)
                setCurrentTabItem(tabItems.indexOfFirst { tabInfo -> tabInfo.type.name == it.defaultTab })
            }
            // 判断之前完善资料是否有设置昵称的步骤，没有就显示弹窗
            if (it?.setNameRequired == false) {
                checkLoginRegistered()
            }
        }
    } else {
        tabItem2TabInfo(tabbarList)
    }
}


@SuppressLint("DefaultLocale")
private fun TabMainActivity.tabItem2TabInfo(itemList: MutableList<TabItem>) {
    val list = arrayListOf<TabInfo>()
    list.addAll(itemList.map { findTabInfo(it) })
    if (tabItems.map { it.type.name } != list.map { it.type.name }) {
        rebuildPager(list, getServiceSelectedPosition(list.map { it.type.name }))
    }
}

private fun getServiceSelectedPosition(types: List<String>): Int {
    val index = types.indexOfFirst { it.equals(tabbarConfig.defaultTab.orEmpty(), ignoreCase = true) }
    return if (index >= 0) index else 0
}

/**
 * 选择上一次退出时的tab
 */
@SuppressLint("DefaultLocale")
private fun getSelectedLastPosition(list: List<TabInfo>): Int {
    val tab = Preference.PREF_KEY_TAB_INDEX_NAME.getStringMMKV(list.firstOrNull()?.type?.name ?: "")
    val position = list.indexOfFirst { TextUtils.equals(tab.toUpperCase(), it.type.name.toUpperCase()) }
    return if (list.inList(position)) position else 0
}

@SuppressLint("DefaultLocale")
fun TabMainActivity.findTabInfo(tabItem: TabItem) =
        when (tabItem.id.toUpperCase()) {
            TAB_ITEM.HOME.name -> TabInfo(R.drawable.tabbar_home_page_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_home.svga", tabItem.name, TAB_ITEM.HOME)
            TAB_ITEM.SHORT_VIDEO.name -> TabInfo(R.drawable.tabbar_short_video_page_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_short_video.svga", tabItem.name, TAB_ITEM.SHORT_VIDEO)
            TAB_ITEM.DISCOVERY.name -> TabInfo(R.drawable.tabbar_discovery_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_discovery.svga", tabItem.name, TAB_ITEM.DISCOVERY)
            TAB_ITEM.MESSAGE.name -> TabInfo(R.drawable.tabbar_message_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_message.svga", tabItem.name, TAB_ITEM.MESSAGE)
            TAB_ITEM.MY.name -> TabInfo(R.drawable.tabbar_me_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_me.svga", tabItem.name, TAB_ITEM.MY)
            else -> TabInfo(R.drawable.tabbar_home_page_img_selector, tabItem.icon0.orEmpty(), tabItem.icon1.orEmpty(), tabItem.animation.orEmpty(), "tab_anim_home.svga", tabItem.name, TAB_ITEM.NONE)
        }

fun TabMainActivity.findFragment(info: TabInfo): Fragment =
        when (info.type) {
            TAB_ITEM.HOME -> IntimateHomeFragment()
            TAB_ITEM.DISCOVERY -> FindTabFragment()
            TAB_ITEM.SHORT_VIDEO -> ShortVideoTabFragment()
            TAB_ITEM.MESSAGE -> MessageFragment()
            TAB_ITEM.MY -> MineTabFragment()
            else -> TabItemFragment()
        }


fun TabMainActivity.changeLogin(isLogin: Boolean) {
    postAction(CHANG_LOGIN, isLogin)
    timUpdateUnreadLabel()
    if (!isLogin) {
        removeCookie(this)
        imLogout(success = {
            timUpdateUnreadLabel()
            resetNotifyAll()
        })
    }
}

/**显示所有的未读消息弹窗*/
fun TabMainActivity.showUnreadMessageDialog() {
    apiSpiceMgr.executeToastKt(MessageUnreadRequest(), success = {
        it.data?.exclusiveApply.orEmpty().forEach { exclusiveApply ->
            if (exclusiveApply.type == "APPLY_EXCLUSIVE") {
                applyExclusive(exclusiveApply.data)
            }
        }
    })
}

/**
 * 清理cookie
 */
@Suppress("DEPRECATION")
private fun removeCookie(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
        CookieSyncManager.createInstance(context)
        CookieManager.getInstance().removeAllCookie()// Removes all cookies.
        CookieSyncManager.getInstance().sync()
    } else {
        CookieManager.getInstance().flush()
    }
}

/**
 * 处理推送通知
 */
fun TabMainActivity.checkImPushNotificationIntent(intent: Intent?) {
    val peer = intent?.getStringExtra(PUSH_MESSAGE_IDENTIFY).orEmpty()
    val isGroup = intent?.getBooleanExtra(PUSH_MESSAGE_IS_GROUP, false) ?: false
    val extraData = intent?.getStringExtra(PUSH_TIM_REPORT_DATA).orEmpty()
    /** 一定要有聊天对象 **/
    if (peer.isNotEmpty()) {
        val chickData = extraData.json2Obj<ChickCustomData>()
        if (chickData != null) {
            val action = chickData.action
            if (chickData.report.isNotEmpty()) {
                apiSpiceMgr.executeKt(MessagePushReport(chickData.report.parseValue("seq_id", "0")))
            }
            when {
                action.matches(AmanLink.URL.jump_to_video_live.toRegex()) -> {
                    apiSpiceMgr.executeToastKt(HomeStatusRequest(chickData.userId), success = { result ->
                        if (result.data?.isLiving == true) Nav.toAmanLink(activity, action)
                        else UIToast.toast(activity, "直播已结束")
                    })
                }
                action.isNotEmpty() -> Nav.toAmanLink(activity, action)
            }
        } else {
            toImChat(peer, isGroup)
        }
    }

}


fun TabMainActivity.loadRoomConfig(config: GenericEffectConfig?) {
    if (config != null) {
        val genericEffectConfig = GenericEffectConfig::class.java.name.getObjectMMKV(GenericEffectConfig::class.java)
        var url: URL? = null
        try {
            url = URL(genericEffectConfig?.redPacket)
        } catch (e: Exception) {
            logDebug(e.toString())
        }
        //先预加载, 提前加载动画进行缓存，当使用时就直接显示了
        url?.let {
            SVGAParser.shareParser().decodeFromURL(it, null)
        }
    }
}