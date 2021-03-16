package com.mei.mentor_home_page.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.commit
import com.gyf.immersionbar.ImmersionBar
import com.mei.orc.ActivityLauncher
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.CustomSupportFragment
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.parseValue
import com.mei.work.ui.WorkRoomMainFragmentLauncher
import com.net.network.chick.friends.UserHomePagerRequest
import kotlinx.android.synthetic.main.mentor_page_activity_layout.*
import launcher.Boom
import launcher.MulField

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/29
 */
class MentorHomePageActivity : MeiCustomActivity() {

    @Boom
    var mUserId: Int = 0

    @Boom
    @MulField
    var mSelectPosition = 0   //默认选择tab

    private var currentFragment: CustomSupportFragment? = null

    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (intent.dataString.orEmpty().isNotEmpty()) {
            mUserId = intent.dataString.orEmpty().parseValue("userid", 0)
            mSelectPosition = intent.dataString.orEmpty().parseValue("selectPosition", 0)
        } else {
            ActivityLauncher.bind(this)
        }
        setContentView(R.layout.mentor_page_activity_layout)
        ImmersionBar.with(this).apply {
            statusBarDarkFont(true)
            transparentStatusBar()
        }.init()
        loadFragment()
        mentor_empty_layout.setBtnClickListener(View.OnClickListener { loadFragment() })
    }

    override fun onNewIntent(newIntent: Intent?) {
        super.onNewIntent(newIntent)
        newIntent?.let { intent ->
            setIntent(intent)
            //初始化默认选择选项
            val userId: Int
            val selectPosition: Int
            if (intent.dataString.orEmpty().isEmpty()) {
                userId = intent.getIntExtra("com.mei.mentor_home_page.ui.mUserIdIntentKey", 0)
                selectPosition = intent.getIntExtra("com.mei.mentor_home_page.ui.mSelectPositionIntentKey", 0)
            } else {
                //走内链
                userId = intent.dataString.orEmpty().parseValue("userid", 0)
                selectPosition = intent.dataString.orEmpty().parseValue("selectPosition", 0)
            }
            if (mUserId != userId || selectPosition != mSelectPosition) {
                // 重新加载fragment
                mUserId = userId
                mSelectPosition = selectPosition
                loadFragment()
            }
        }
    }


    private fun loadFragment() {
        apiSpiceMgr.requestImUserInfo(arrayOf(mUserId), true)
        showLoadingCover()
        apiSpiceMgr.executeKt(UserHomePagerRequest(mUserId), success = {
            if (it.isSuccess && it.data != null) {
                mentor_empty_layout.isVisible = false
                supportFragmentManager.commit(allowStateLoss = true) {
                    val fragment = if (it.data.isGroup) WorkRoomMainFragmentLauncher.newInstance(mUserId, mSelectPosition).apply {
                        pagerResult = it.data
                    }
                    else MentorHomePageFragmentLauncher.newInstance(mUserId, mSelectPosition).apply {
                        pagerResult = it.data
                    }
                    currentFragment = fragment
                    replace(R.id.page_container, fragment)
                }
            } else {
                mentor_empty_layout.isVisible = true
            }
        }, failure = {
            mentor_empty_layout.isVisible = true
        }, finish = { showLoading(false) })
    }
}