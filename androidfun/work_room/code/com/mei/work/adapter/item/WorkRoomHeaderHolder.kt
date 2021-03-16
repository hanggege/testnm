package com.mei.work.adapter.item

import android.content.Intent
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.flyco.roundview.RoundRelativeLayout
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.mei.base.common.WORK_ROOM_CHANGED
import com.mei.live.LiveEnterType
import com.mei.live.ui.VideoLiveRoomActivityLauncher
import com.mei.live.ui.ext.getCurrLiveState
import com.mei.mentor_home_page.ui.dialog.showMentorBrowser
import com.mei.orc.event.postAction
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.picker.pickerImage
import com.mei.video.pickerVideo
import com.mei.widget.holder.activity
import com.mei.widget.holder.addOnClickListener
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.work.adapter.ThumbnailAdapter
import com.mei.work.adapter.layoutmanager.ThumbnailLayoutManager
import com.mei.work.ui.WorkRoomInfoEditorActivity
import com.net.model.chick.friends.ProductBean
import com.net.model.chick.friends.UserHomePagerResult
import com.net.network.chick.friends.WorkListRequest

/**
 * WorkRoomHeaderHolder
 *
 * 工作室顶部封面+信息概览
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-07-30
 */
class WorkRoomHeaderHolder(view: View) : WorkRoomBaseHolder(view) {

    var pagerResult = UserHomePagerResult()
    val mThumbnailData = arrayListOf<ProductBean>()
    val mThumbnailAdapter by lazy {
        ThumbnailAdapter(mThumbnailData).apply {
            setOnItemClickListener { _, _, position ->
                val item = mThumbnailData.getOrNull(position)
                val isEmpty = item?.cover.orEmpty().isEmpty() && item?.url.orEmpty().isEmpty()
                if (checkPagerInfo() && !isEmpty) {
                    val mentorId = pagerResult.bindPromoter?.userId ?: pagerResult.info?.userId
                    pagerResult.works?.let { works ->
                        /** 查看完整相册，编辑相册  **/
                        (activity as? FragmentActivity)?.showMentorBrowser(mentorId
                                ?: 0, works, position,
                                arrayListOf(pagerResult.info?.homepageCover).filterNotNull(),
                                editEnable = pagerResult.canEditGroupInfo,
                                showBottomInfo = !pagerResult.isGroupMember,
                                topWorksLimit = pagerResult.groupTopWorksLimit) {
                            /** 内部进行了删除，置顶等操作，需要刷新当前数据 **/
                            works.worksCount = it.worksCount
                            works.hasMore = it.hasMore
                            works.nextPageNo = it.nextPageNo
                            works.list = it.list
                            refreshAdapter()
                        }
                    }
                }
            }
        }
    }


    init {
        getView<RecyclerView>(R.id.room_main_thumbnail_rv).let { rv ->
            rv.adapter = mThumbnailAdapter
            rv.layoutManager = ThumbnailLayoutManager(rv.context)
            GravitySnapHelper(Gravity.START).apply { snapToPadding = true }.attachToRecyclerView(rv)
            rv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && pagerResult.works?.hasMore == true) {
                        /** 预加载更多照片 **/
                        val works = pagerResult.works
                        val lastVisiblePosition = (rv.layoutManager as? LinearLayoutManager)?.findLastVisibleItemPosition()
                                ?: 0
                        if (!rv.isActivated && works != null && lastVisiblePosition > mThumbnailAdapter.itemCount - 3) {
                            rv.isActivated = true
                            apiSpiceMgr.executeKt(WorkListRequest(pagerResult.info?.userId ?: 0,
                                    works.nextPageNo), success = {
                                it.data?.let { data ->
                                    pagerResult.worksCount = data.count
                                    pagerResult.works?.worksCount = data.count
                                    pagerResult.works?.nextPageNo = data.list.nextPageNo
                                    pagerResult.works?.hasMore = data.list.hasMore
                                    pagerResult.works?.list?.addAll(data.list.list)
                                    refreshAdapter()
                                }
                            }, finish = {
                                rv.isActivated = false
                            })
                        }
                    }
                }
            })
        }
        getView<View>(R.id.room_main_upload_cover).setOnClickListener {
            if (checkPagerInfo()) {
                uploadHomePageHeadImg()
            }
        }
        getView<View>(R.id.room_main_upload_video).setOnClickListener {
            if (checkPagerInfo()) {
                (activity as? FragmentActivity)?.pickerVideo(1) { uploadVideoToQiniu(it.mapNotNull { it.path }) }
            }
        }
        getView<View>(R.id.room_main_upload_photo).setOnClickListener {
            if (checkPagerInfo()) {
                (activity as? FragmentActivity)?.pickerImage(9) { uploadImageToQiniu(it) }
            }
        }
        getView<View>(R.id.room_main_edit).setOnClickListener {
            //编辑资料
            (activity as? FragmentActivity)?.startFragmentForResult(Intent(activity, WorkRoomInfoEditorActivity::class.java), 120) { code, _ ->
                if (code == 120) {
                    postAction(WORK_ROOM_CHANGED)
                }
            }
        }

        getView<View>(R.id.room_main_living_label).setOnClickNotDoubleListener(tag = "work_room_live_id") {
            when (getCurrLiveState()) {
                1 -> UIToast.toast("正在直播中，无法查看")
                2 -> UIToast.toast("正在上麦中，无法查看")
                else -> VideoLiveRoomActivityLauncher.startActivity(activity, pagerResult.info?.roomId, LiveEnterType.user_home_page)
            }
        }

        getView<View>(R.id.room_main_header_room_id).setOnClickNotDoubleListener(tag = "work_room_live_id") {
            VideoLiveRoomActivityLauncher.startActivity(activity, pagerResult.info?.roomId, LiveEnterType.user_home_page)
        }
    }

    override fun refresh(item: Any) {
        addOnClickListener(R.id.room_main_header_label)
        val data = (item as? WorkRoomHeaderData)?.result
        getView<View>(R.id.status_bar_view).apply {
            if (layoutParams != null) updateLayoutParams {
                height = getStatusBarHeight()
            }
        }
        val isChanged = data != pagerResult
        if (data is UserHomePagerResult) pagerResult = data

        setGone(R.id.room_main_edit_group, !pagerResult.canEditGroupInfo)
        setVisible(R.id.room_main_header_room_id, pagerResult.hasLived)
        setVisible(R.id.room_main_header_room_id_arrow, pagerResult.hasLived)
        if (isChanged) {
            pagerResult.works?.worksCount = pagerResult.worksCount
            refreshAdapter()
        }
        refreshLiveData()
    }

    private fun refreshLiveData() {
        // Summary
        setText(R.id.room_main_summary_title, pagerResult.info?.groupInfo?.nickname.orEmpty())
        (pagerResult.roomStatus != null).apply {
            getView<RoundRelativeLayout>(R.id.room_main_living_label).isVisible = this && !pagerResult.isGroupMember
            getView<ImageView>(R.id.room_main_living_label_avatar).isVisible = this && !pagerResult.isGroupMember
            getView<View>(R.id.room_main_header_label).isGone = this || pagerResult.isGroupMember
            if (this) {
                getView<ImageView>(R.id.room_main_living_label_avatar).glideDisplay(pagerResult.roomStatus?.personalImage, pagerResult.roomStatus?.gender.genderPersonalImageLiving())
            }
        }
        setText(R.id.room_main_header_room_id, "房间ID:${pagerResult.info?.roomId}")
    }

    fun refreshAdapter() {
        mThumbnailData.clear()
        /** 第一个图片是工作室展示图 **/
        mThumbnailData.add(ProductBean().apply {
            countAll = pagerResult.works?.worksCount ?: pagerResult.worksCount
            worksType = 0
            url = pagerResult.info?.homepageCover.orEmpty()
            cover = url
        })
        if (pagerResult.works?.list.orEmpty().isNotEmpty()) {
            mThumbnailData.addAll(pagerResult.works?.list.orEmpty())
        } else if (pagerResult.canEditGroupInfo) {
            // 没有的话加一个空UI
            mThumbnailData.add(ProductBean())
        }
        mThumbnailAdapter.notifyDataSetChanged()
    }

    private fun checkPagerInfo() = pagerResult.info != null
}

@DrawableRes
fun Int?.genderPersonalImageLiving(): Int = when (this) {
    0 -> R.drawable.personal_img_female_living
    1 -> R.drawable.personal_img_male_living
    else -> R.drawable.personal_img_male_living
}

class WorkRoomHeaderData(val result: UserHomePagerResult)