package com.mei.video.ui

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.base.screenHeight
import com.mei.base.screenWidth
import com.mei.video.model.VideoMediaEntity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */


/** 已经选中的图片，进入当前界面需要清空 **/
internal val selectedVideo = arrayListOf<VideoMediaEntity>()

/** 当且只有[selectMax]等于1 时才有拍照 **/
internal var selectVideoMax = 9
internal fun putVideo(item: VideoMediaEntity): Boolean {
    return if (selectedVideo.size < selectVideoMax) {
        selectedVideo.add(item)
        true
    } else {
        false
    }
}

internal fun Context.pickerVideoIntent(limit: Int = 9, defaultSelected: List<String>) = Intent(this, VideoPickerActivity::class.java)
        .apply {
            putExtra(PICKER_MAX_LIMIT_Video, limit)
            val list = arrayListOf<String>()
            defaultSelected.forEach { list.add(it) }
            putStringArrayListExtra(PICKER_SELECTED_DEFAULT_Video, list)
        }

internal const val PICKER_SELECTED_DEFAULT_Video = "picker_selected_default"
internal const val PICKER_MAX_LIMIT_Video = "picker_max_limit"
internal const val PICKER_RESULT_Video = "picker_result"


internal fun VideoPickerActivity.createFolderPopWindow(): PopupWindow {
    val popView = RecyclerView(this).apply {
        layoutManager = LinearLayoutManager(this@createFolderPopWindow)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        adapter = folderVideoAdapter
        setBackgroundColor(Color.WHITE)
    }
    return PopupWindow(popView, screenWidth,
            screenHeight * 1 / 3).apply {
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.WHITE))
    }

}