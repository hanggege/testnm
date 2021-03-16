package com.mei.message.ext

import androidx.core.view.isVisible
import com.mei.message.ConversationFragment
import com.mei.message.MessageFragment
import com.mei.message.VisitorsFragment
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import kotlinx.android.synthetic.main.fragment_message.*

/**
 *
 * @author Created by lenna on 2020/6/12
 */

fun MessageFragment.loadMessagePage(isLogin: Boolean) {
    if (isLogin) {
        MeiUser.resetUser(apiSpiceMgr, object : RequestListener<ChickUserInfo.Response> {
            override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
                loadBosom(false)
            }

            override fun onRequestSuccess(result: ChickUserInfo.Response) {
                loadBosom(MeiUser.getSharedUser().info?.isPublisher == true)
            }
        })
    } else {
        loadBosom(false)
    }
}

fun MessageFragment.loadBosom(isPublisher: Boolean) {
    if (isAdded) {
        titleList.clear()
        fragmentList.clear()
        msg_tab_menu_container.isVisible = isPublisher
        message_title.isVisible = !isPublisher
        titleList.add("私聊")
        fragmentList.add(ConversationFragment())
        if (isPublisher) {
            titleList.add("访客")
            fragmentList.add(VisitorsFragment())
        }
        selectedMenu(0)
        adapter.setFragmentsAndTitles(fragmentList, titleList)
    }
}


