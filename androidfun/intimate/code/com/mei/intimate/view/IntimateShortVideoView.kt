package com.mei.intimate.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.base.common.SHORT_VIDEO_LIST_REFRESH
import com.mei.base.common.SHORT_VIDEO_LIST_TAB_CHANGE
import com.mei.base.ui.nav.Nav
import com.mei.live.manager.genderAvatar
import com.mei.login.toLogin
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.orc.ext.screenWidth
import com.mei.orc.ext.subListByIndex
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.video.ShortVideoPlayerNewActivityLauncher
import com.mei.wood.R
import com.net.model.chick.room.RoomList.ShortVideoItemBean
import com.net.model.chick.room.RoomList.ShortVideoItemBean.VideoTag
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import kotlinx.android.synthetic.main.view_intimate_short_video.view.*

/**
 * Created by hang on 2020/7/10.
 */
class IntimateShortVideoView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        RelativeLayout(context, attrs, defStyleAttr) {

    private var videoItem: ShortVideoItemBean? = null

    var currentPosition = 0
    var currTagId = -1
    var fromType: String = "首页"

    private val tagList = arrayListOf<VideoTag>()
    private val tagAdapter by lazy {
        object : TagAdapter<VideoTag>(tagList) {
            override fun getView(parent: FlowLayout, position: Int, t: VideoTag): View {
                return TextView(parent.context).apply {
                    textSize = 12f
                    setTextColor(Cxt.getColor(R.color.color_FF9200))
                    layoutParams = LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    setPadding(dip(5))
                    includeFontPadding = false
                    text = t.name.orEmpty()
                }
            }
        }
    }

    init {
        layoutInflaterKtToParentAttach(R.layout.view_intimate_short_video)


        short_video_tag_list.adapter = tagAdapter

        short_video_cover.setOnClickNotDoubleListener(tag = "video_specific") {
            turn2VideoSpecific()
        }
        short_video_title.setOnClickNotDoubleListener(tag = "video_specific") {
            turn2VideoSpecific()
        }

        short_video_tag_list.setOnTagClickListener { _, position, _ ->
            if (currTagId == videoItem?.tags?.get(position)?.seqId) { // 当前标签页
                postAction(SHORT_VIDEO_LIST_REFRESH, currTagId)
            } else {
                postAction(SHORT_VIDEO_LIST_TAB_CHANGE, videoItem?.tags?.get(position)?.seqId)
            }
            GrowingUtil.track("main_app_gn_use_data",
                    "screen_name", fromType,
                    "main_app_gn_type", "短视频",
                    "main_app_gn_label", "底部标签",
                    "position", "",
                    "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
            Nav.toAmanLink(context, videoItem!!.tags[position].action.orEmpty())
            true
        }
    }


    private fun turn2VideoSpecific() {
        if (JohnUser.getSharedUser().hasLogin()) {
            GrowingUtil.track("main_app_gn_use_data",
                    "screen_name", fromType,
                    "main_app_gn_type", "短视频",
                    "main_app_gn_label", "短视频详情入口",
                    "position", (currentPosition + 1).toString(),
                    "time_stamp", (System.currentTimeMillis()/ com.mei.orc.unit.TimeUnit.SECOND).toString())
            ShortVideoPlayerNewActivityLauncher.startActivity(context, videoItem?.seqId.orEmpty()
                    , getTags(), false, currTagId == 0, videoItem?.videoCoverUrl, "home")
        } else {
            context.toLogin()
        }
    }

    private fun getTags(): String {
        return if (currTagId >= 0) {
            currTagId.toString()
        } else {
            videoItem?.tags?.firstOrNull().let {
                it?.seqId?.toString() ?: ""
            }
        }
    }

    fun convertData(data: ShortVideoItemBean?) {
        data?.let {
            videoItem = data
            short_video_cover.updateLayoutParams {
                width = screenWidth / 2
                height = if (data.videoWidth > 0) data.videoHeight * width / data.videoWidth else width
            }
            short_video_cover.glideDisplay(data.videoCoverUrl, R.color.color_f0f0f0)

            short_video_avatar.glideDisplay(it.user?.avatar.orEmpty(), it.user?.gender.genderAvatar(it.user?.isPublisher))
            short_video_consultant.text = it.user?.nickName.orEmpty()
            short_video_title.text = it.title.orEmpty()

            if (!data.tags.isNullOrEmpty()) {
                val tagSubList = data.tags.subListByIndex(2)
                tagList.clear()
                tagList.addAll(tagSubList)
                tagAdapter.notifyDataChanged()
            }
        }
    }
}