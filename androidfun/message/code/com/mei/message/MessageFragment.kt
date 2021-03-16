package com.mei.message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.*
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import com.joker.im.*
import com.joker.im.custom.CustomType
import com.joker.im.listener.IMAllEventManager
import com.joker.im.message.CustomMessage
import com.mei.GrowingUtil
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.ui.nav.Nav
import com.mei.base.weight.fragmentpager.CustomFragmentPagerAdapter
import com.mei.im.ext.isCmdId
import com.mei.im.quickLoginIM
import com.mei.im.resetNotifyAll
import com.mei.message.ext.loadMessagePage
import com.mei.orc.event.bindAction
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.john.model.JohnUser
import com.mei.orc.tab.TabItemContent
import com.mei.orc.util.string.getInt
import com.mei.orc.util.string.maxNinetyNine
import com.mei.wood.R
import com.mei.wood.cache.getCacheUserInfo
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.MeiUser
import com.net.network.chick.message.MessageBannerRequest
import com.tencent.imsdk.TIMConversationType
import com.tencent.imsdk.TIMManager
import kotlinx.android.synthetic.main.fragment_message.*
import kotlinx.android.synthetic.main.include_msg_tab_item.view.*

/**
 * author : Song Jian
 * date   : 2020/2/13
 * desc   : 消息Tab页
 */

class MessageFragment : TabItemFragment(), TabItemContent {

    val titleList = arrayListOf<String>() //标题集合，导师的标题会需要用到
    val fragmentList: ArrayList<BaseBannerFragment> = arrayListOf()  //消息tab展示的fragment
    val adapter: CustomFragmentPagerAdapter by lazy { CustomFragmentPagerAdapter(childFragmentManager) }
    private val msgReceiver: IMAllEventManager by lazy {
        object : IMAllEventManager() {
            override fun onRefresh() {
                super.onRefresh()
                refreshUnReadNum()
            }
        }
    }
    private val notifyConversation by lazy { TIMManager.getInstance().getConversation(TIMConversationType.C2C, SYSTEM_NOTIFY_USER_ID).mapToConversation() }
    private val pageChangeListener by lazy {
        object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                refreshUnReadNum()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_message, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        msgReceiver.bindEventLifecycle(this)
        view.apply { updatePadding(top = getStatusBarHeight()) }
        dove_msg_view_pager.adapter = adapter
        dove_msg_view_pager.addOnPageChangeListener(pageChangeListener)
        if (JohnUser.getSharedUser().hasLogin()) {
            if (!imIsLogin()) quickLoginIM(success = { fragmentList.forEach { it.refreshIMList() } })
        }
        loadMessagePage(JohnUser.getSharedUser().hasLogin())
        bindAction<Boolean>(CHANG_LOGIN) {
            loadMessagePage(it == true)
        }
        initSetListener()
    }

    private fun initSetListener() {
        notification_icon_fl.setOnClickListener {
            notifyConversation.readAllMessage()
            notify_red_dot_rll.isVisible = false
            Nav.toAmanLink(context, AmanLink.URL.jump_message_notification_page)
        }
    }


    override fun onResume() {
        super.onResume()
        willAppear()
    }

    override fun willAppear() {
        super.willAppear()
        refreshUnReadNum()
        refreshBanner()
        fragmentList.forEach {
            (it as? TabItemFragment)?.willAppear()
        }
        resetNotifyAll()
    }

    private fun refreshUnReadNum() {
        loadNotificationRedDot()
        selectedMenu(dove_msg_view_pager.currentItem)
    }

    /**
     * 加载通知红点
     */
    private fun loadNotificationRedDot() {
        val isLogin = JohnUser.getSharedUser().hasLogin()
        if (SYSTEM_NOTIFY_USER_ID == notifyConversation.timConversation.peer) {
            val unReadMsgNum = notifyConversation.timConversation.unreadMessageNum
            val isVisibleDot = unReadMsgNum > 0 && isLogin
            notify_red_dot_rll.isVisible = isVisibleDot
        }
    }

    /**
     * 刷新banner数据，这里估计得改一下，直接把数据请求放到ConversationFragment中去
     */
    private fun refreshBanner() {
        apiSpiceMgr.executeKt(MessageBannerRequest(), success = { response ->
            fragmentList.forEach {
                if (it is ConversationFragment) {
                    it.bannerList = response.data?.banner.orEmpty()
                    it.buildHeader()
                }
            }
        })
    }

    /**
     *  切换tab
     */
    fun selectedMenu(index: Int) {
        if (isAdded) {
            msg_tab_menu_container.forEachIndexed { i, view ->
                view.apply {
                    setOnClickListener { dove_msg_view_pager.setCurrentItem(i, true) }
                    tab_item_title.apply {
                        textSize = if (index == i) 21f else 16f
                        paint.isFakeBoldText = index == i
                        text = titleList.getIndexOrNull(i).orEmpty()
                    }
                    tab_bottom_line.isInvisible = index != i
                    val isLogin = JohnUser.getSharedUser().hasLogin()
//                red_point_view1.text = if (i == 1 && notifyConversation.timConversation.unreadMessageNum > 0 && isLogin) " " else ""
                    val count = TIMManager.getInstance().conversationList?.filter {
                        it.type == TIMConversationType.C2C
                                && it.peer != SYSTEM_NOTIFY_USER_ID
                                && !it.peer.toIntOrNull().isCmdId()
                                && getCacheUserInfo(it.peer.getInt(0))?.isBlackList != true
                    }?.filter { it.peer != imLoginId() }?.sumBy {
                        var count = 0
                        if (it.mapToConversation().timConversation.lastMsg != null
                                && ((it.mapToConversation().timConversation.lastMsg.mapToMeiMessage()
                                        as? CustomMessage)?.customMsgType
                                        == CustomType.exclusive_system_notify)
                                && !it.mapToConversation().timConversation.lastMsg.isRead) {
                            count = 1
                        }
                        it.unreadMessageNum.toInt() - count
                    }

                    if (count != null) {
                        red_point_view2.text = if (i != 0 || count <= 0 || !isLogin) "" else count.maxNinetyNine()
                    }
                    red_point_view2.isVisible = red_point_view2.text.isNotEmpty()
                    /**需要等这个view绘制完了再去更改他的参数*/
                    red_point_view2.post {
                        red_point_view2.updateLayoutParams {
                            width = when (red_point_view2.text.length) {
                                0 -> 0
                                1 -> red_point_view2.height
                                else -> red_point_view2.height + (red_point_view2.textSize * (red_point_view2.text.length - 1) * 3 / 5).toInt()
                            }

                        }
                    }
                }
            }
        }
    }

    override fun onSelect() {
        GrowingUtil.track("main_page_view",
                "screen_name", "消息",
                "main_app_gn_pro", MeiUser.getSharedUser().info?.interests.orEmpty().joinToString { it.name },
                "main_app_gn_type", "消息，访客",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
    }

    override fun onDeSelect() {

    }

    override fun onReSelect() {

    }

    /**
     * 获取当前处于tab索引
     */
    fun getCurrentIndex(): Int? {
        return if (isAdded) dove_msg_view_pager.currentItem else 0
    }
}