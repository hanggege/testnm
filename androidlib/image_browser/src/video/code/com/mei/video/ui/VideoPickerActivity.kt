package com.mei.video.ui

import android.app.Activity
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.base.dip
import com.mei.picker.R
import com.mei.picker.ui.PICKER_MAX_LIMIT
import com.mei.picker.ui.PICKER_RESULT
import com.mei.picker.ui.PICKER_SELECTED_DEFAULT
import com.mei.video.adapter.FolderVideoPickerAdapter
import com.mei.video.adapter.VideoPickerAdapter
import com.mei.video.loader.VideoAlbumLoader
import com.mei.video.model.VideoAlbum
import com.mei.video.model.VideoMediaEntity
import com.mei.video.tokeVideo
import kotlinx.android.synthetic.main.picker_video_layout_activity.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */


internal class VideoPickerActivity : AppCompatActivity() {

    private val defaultSelectedList = arrayListOf<String>() // app初始被选择的条目
    private val photoAlbum = arrayListOf<VideoAlbum>()
    private val photoList = arrayListOf<VideoMediaEntity?>()
    private val pickerAdapter: VideoPickerAdapter by lazy {
        val adapter = VideoPickerAdapter(photoList) { position, item ->
            if (position == 0 && item == null) {
                tokeVideo {
                    if (it != null) {
                        putVideo(VideoMediaEntity().apply {
                            path = it.path
                            id = it.path
                        })
                        commit_view.performClick()
                    } else Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show()
                }
            }
        }
        adapter
    }
    private val adapterObserver: RecyclerView.AdapterDataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                commit_view.text = if (selectedVideo.size > 0) "使用 (${selectedVideo.size}/$selectVideoMax)" else "请选择"
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                commit_view.text = if (selectedVideo.size > 0) "使用 (${selectedVideo.size}/$selectVideoMax)" else "请选择"
            }
        }
    }
    private val photoLoader: VideoAlbumLoader by lazy {
        VideoAlbumLoader(this) {
            /** 从手机获取到了所有的专辑 **/
            photoAlbum.clear()
            photoList.clear()
            photoAlbum.addAll(it)
            if (selectVideoMax == 1) photoList.add(null)
            photoAlbum.forEach { img -> photoList.addAll(img.videoList) }
            photoAlbum.add(0, VideoAlbum().apply {
                bucketName = "所有视频"
                videoList.addAll(photoList.filterNotNull())
            })
            selectedVideo.addAll(photoList.filterNotNull().filter { p -> defaultSelectedList.contains(p.path) })
            pickerAdapter.notifyDataSetChanged()
            folderVideoAdapter.notifyDataSetChanged()
            folderPop.height = photoAlbum.size * this.dip(40)
        }
    }
    val folderVideoAdapter: FolderVideoPickerAdapter by lazy {
        FolderVideoPickerAdapter(photoAlbum) {
            photoList.clear()
            if (selectVideoMax == 1) photoList.add(null)
            photoList.addAll(it.videoList)
            pickerAdapter.notifyDataSetChanged()
            folderPop.dismiss()
            category_btn.text = it.bucketName

        }

    }
    private val folderPop: PopupWindow by lazy { createFolderPopWindow() }

    private val preViewFragment: VideoPreviewFragment? by lazy {
        supportFragmentManager.findFragmentById(R.id.preview_fragment) as? VideoPreviewFragment
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectVideoMax = intent.getIntExtra(PICKER_MAX_LIMIT, selectVideoMax)
        defaultSelectedList.clear()
        defaultSelectedList.addAll(
                intent.getStringArrayListExtra(PICKER_SELECTED_DEFAULT) ?: arrayListOf())
        selectedVideo.clear()
        setContentView(R.layout.picker_video_layout_activity)
        supportActionBar?.hide()
        pocker_image_video_title.text = "选择视频"
        category_btn.text = "所有视频"
        picker_recycler.layoutManager = GridLayoutManager(this, 3)
        picker_recycler.adapter = pickerAdapter
        photoLoader.execute()
        preViewFragment?.selectBack = { pickerAdapter.notifyDataSetChanged() }
        category_btn.setOnClickListener {
            folderPop.showAsDropDown(footer_layout)
        }
        footer_layout.isClickable = true
        back_view.setOnClickListener {
            finish()
        }
        photo_show_tv.setOnClickListener {
            if (selectedVideo.isNotEmpty()) {
                preViewFragment?.showPreView(selectedVideo, 0)
            }
        }
        commit_view.setOnClickListener {
            setResult(Activity.RESULT_OK, Intent().apply {
                val list = arrayListOf<VideoMediaEntity>()
                selectedVideo.forEach {
                    list.add(it)
                }
                putParcelableArrayListExtra(PICKER_RESULT, list)
            })

            finish()
        }
        pickerAdapter.registerAdapterDataObserver(adapterObserver)
    }

    override fun onBackPressed() {
        val fragment = preViewFragment
        if (fragment != null && fragment.showPre) {
            preViewFragment?.showPreView(show = false)

        } else {
            super.onBackPressed()
        }

    }

    override fun onDestroy() {
        // 如果Task还在运行，则先取消它，然后执行关闭activity代码
        if (photoLoader.status == AsyncTask.Status.RUNNING) {
            photoLoader.cancel(true)
        }
        super.onDestroy()
        pickerAdapter.unregisterAdapterDataObserver(adapterObserver)
    }


}