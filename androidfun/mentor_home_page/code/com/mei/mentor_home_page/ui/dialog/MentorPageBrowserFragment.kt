package com.mei.mentor_home_page.ui.dialog

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.View
import android.view.ViewGroup
import androidx.core.transition.doOnEnd
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import com.gyf.immersionbar.ImmersionBar
import com.mei.base.common.MUTE_REMOTE_AUDIO_STREAMS
import com.mei.chat.toImChat
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.isNotOnDoubleClick
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.video.browser.adapter.MultiMediaData
import com.mei.wood.BuildConfig
import com.mei.wood.R
import com.mei.wood.cache.requestImUserInfo
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.CustomDialogFragment
import com.net.model.chick.friends.WorkListResult
import com.net.model.chick.tab.tabbarConfig
import com.net.network.chick.friends.WorkListRequest
import com.net.network.works.WorksDeleteRequest
import com.net.network.works.WorksTopRequest
import com.pili.player
import kotlinx.android.synthetic.main.mentor_browser_layout.*
import kotlinx.android.synthetic.main.mentor_browser_menu_layout.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/25
 */
fun FragmentActivity?.showMentorBrowser(mentorId: Int,
                                        workListResult: WorkListResult.WorksBean? = null,
                                        index: Int,
                                        firstList: List<String> = arrayListOf(),
                                        editEnable: Boolean = mentorId == JohnUser.getSharedUser().userID,
                                        showBottomInfo: Boolean = true,
                                        topWorksLimit: Int = 4,
                                        back: (result: WorkListResult.WorksBean) -> Unit = {}) {
    if (getCurrLiveState() > 0) {
        UIToast.toast("当前正在连线中，无法查看")
    } else if (this?.supportFragmentManager != null) {
        isNotOnDoubleClick(1000L, "showMediaBrowser") {
            MentorPageBrowserFragment().apply {
                workList = workListResult ?: WorkListResult.WorksBean()
                firstStickyList = firstList
                backResult = back
                userId = mentorId
                menuEditEnable = editEnable
                showUserInfoBar = showBottomInfo
                initPosition = index
                mTopWorksLimit = topWorksLimit
                show(supportFragmentManager, "MentorPageBrowserFragment")
            }
        }
    }
}

class MentorPageBrowserFragment : CustomDialogFragment() {

    var userId = 0
    var firstStickyList: List<String> = arrayListOf()
    var initPosition: Int = 0
    var menuEditEnable = false
    var showUserInfoBar = true
    var workList: WorkListResult.WorksBean = WorkListResult.WorksBean()
    var backResult: (WorkListResult.WorksBean) -> Unit = {}
    var mTopWorksLimit: Int = 4
    override fun isFullScreen(): Boolean = true

    override fun onSetInflaterLayout(): Int = R.layout.mentor_browser_layout

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        if (BuildConfig.DEBUG) media_browser.pagerRecycler.currentPosition = 2
        if (workList.list.isNotEmpty() || firstStickyList.isNotEmpty()) resetBrowser(initPosition)
        else requestWorkList()

        apiSpiceMgr.requestUserInfo(arrayOf(userId)) {
            val info = it.firstOrNull()
            mentor_info_group.isGone = info?.userId == JohnUser.getSharedUser().userID || activity is VideoLiveRoomActivity
                    || menuEditEnable || !showUserInfoBar
            mentor_avatar_img.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
            mentor_nick_name_tv.text = info?.nickname.orEmpty()
            mentor_to_chat.setOnClickListener { toChat() }
        }
        postAction(MUTE_REMOTE_AUDIO_STREAMS, true)
        player.pause()
    }

    private fun toChat() {
        apiSpiceMgr.requestImUserInfo(arrayOf(userId), needRefresh = true, back = {
            val info = it.firstOrNull()
            when {
                info?.isSelfToBlackList == true -> {
                    UIToast.toast(tabbarConfig.publisherBlacklistUser)
                }
                info?.isTheOtherToBlackList == true -> {
                    UIToast.toast(tabbarConfig.hasBlacklistTips)
                }
                else -> {
                    activity?.toImChat(userId.toString())
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        media_browser.setOnViewPagerListener { _, new, _ ->
            val count = workList.worksCount + firstStickyList.size
            browser_index_txt.text = "${new + 1}/${if (count > 0) count else media_browser.dataList.size}"
            val itemData = workList.list.getOrNull(new - firstStickyList.size)
            browser_stick_top_tv.text = if (itemData?.isTop == 1) "取消置顶" else "置顶"
            more_menu_view.isInvisible = !menuEditEnable || firstStickyList.contains(media_browser.dataList.getOrNull(new)?.url.orEmpty())
            if (new >= workList.list.size - 3) {
                requestWorkList()
            }
            mentor_short_video_show_id_tv.text = "视频ID：${itemData?.seqId}"
            mentor_short_video_show_id_tv.isVisible = itemData?.worksType == 1 || itemData?.worksType == 4
            self_short_video_show_id_tv.isVisible = (userId == JohnUser.getSharedUser().userID || !showUserInfoBar)
                    && (itemData?.worksType == 1 || itemData?.worksType == 4)
            self_short_video_show_id_tv.text = "视频ID：${itemData?.seqId}"
        }
        back_btn.setOnClickListener { dismissAllowingStateLoss() }
        browser_delete_tv.setOnClickNotDoubleListener { deleteItem() }
        browser_stick_top_tv.setOnClickNotDoubleListener { stickTopItem() }
        more_menu_view.setOnClickListener {
            menu_container_group.isVisible = true
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition())
            menu_container_layout.updateLayoutParams { height = dip(92) }
        }
        menu_alpha_bg.setOnClickListener {
            TransitionManager.beginDelayedTransition(view as ViewGroup, AutoTransition().apply {
                doOnEnd {
                    if (isAdded) menu_container_group.isVisible = false
                }
            })
            menu_container_layout.updateLayoutParams { height = 1 }
        }
    }

    private fun requestWorkList(size: Int = 6) {
        if (workList.hasMore && !media_browser.isActivated) {
            media_browser.isActivated = true
            apiSpiceMgr.executeKt(WorkListRequest(userId, workList.nextPageNo, size), success = {
                it.data?.apply {
                    workList.worksCount = count
                    workList.hasMore = list.hasMore
                    workList.nextPageNo = list.nextPageNo
                    workList.list.addAll(list.list.orEmpty())
                    media_browser.dataList.addAll(list.list.orEmpty().map { MultiMediaData(it.worksType, it.cover, it.url, it) })
                    if (workList.list.size == list.list.size) resetBrowser()
                    else media_browser.mediaAdapter.notifyItemRangeInserted(workList.list.size - list.list.size, list.list.size)
                }
            }, finish = {
                media_browser.isActivated = false
            })
        }
    }

    private fun resetBrowser(position: Int = -1) {
        val index = when {
            position > -1 -> position
            media_browser.currentPosition > workList.list.size - 1 -> workList.list.size - 1
            media_browser.currentPosition in 0 until workList.list.size -> media_browser.currentPosition
            else -> 0
        }
        val list = arrayListOf<MultiMediaData>().apply {
            addAll(firstStickyList.map { MultiMediaData(0, it) })
            addAll(workList.list.map { MultiMediaData(it.worksType, it.cover, it.url, it) })
        }
        media_browser.resetData(list, index)
    }

    private fun deleteItem() {
        menu_alpha_bg.performClick()
        showLoading(true)
        val position = media_browser.currentPosition - firstStickyList.size
        workList.list.getOrNull(position)?.let { data ->
            apiSpiceMgr.executeToastKt(WorksDeleteRequest(data.seqId.toString(), workList.nextPageNo), success = { res ->
                if (res.isSuccess) {
                    workList.worksCount = workList.worksCount - 1
                    workList.list.removeAt(position)
                    media_browser.removeAt(position + firstStickyList.size)
                    res.data?.works?.let { bean ->
                        workList.list.add(bean)
                        media_browser.addElement(MultiMediaData(bean.worksType, bean.cover, bean.url, bean))
                    }
                    if (workList.worksCount == 0) {
                        dismissAllowingStateLoss()
                    }
                }
            }, finish = { showLoading(false) })
        }
    }

    private fun stickTopItem() {
        menu_alpha_bg.performClick()
        workList.list.getOrNull(media_browser.currentPosition - firstStickyList.size)?.let { bean ->
            val isTop = if (bean.isTop == 1) 0 else 1
            showLoading(true)
            apiSpiceMgr.executeToastKt(WorksTopRequest(bean.seqId.toString(), isTop), success = {
                if (it.isSuccess) {
                    val topList = workList.list.filter { work -> work.isTop == 1 }
                    if (topList.size >= mTopWorksLimit) {
                        topList.lastOrNull()?.isTop = if (topList.lastOrNull()?.isTop == 1) 0 else 1
                    }
                    bean.isTop = isTop
                    if (isTop == 1) {
                        workList.list.remove(bean)
                        workList.list.add(0, bean)
                        // 最后一张图置顶要定位在原位置
                        if (media_browser.currentPosition == workList.list.size) resetBrowser(workList.list.size)
                        else resetBrowser()
                    } else {
                        val size = workList.list.size
                        workList.hasMore = true
                        workList.list.clear()
                        workList.nextPageNo = 0
                        requestWorkList(size)
                    }
                } else {
                    UIToast.toast(it.errMsg)
                }
            }, failure = { UIToast.toast(it?.currMessage) }, finish = { showLoading(false) })

        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        postAction(MUTE_REMOTE_AUDIO_STREAMS, false)
        backResult(workList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ImmersionBar.destroy(this)
    }

}