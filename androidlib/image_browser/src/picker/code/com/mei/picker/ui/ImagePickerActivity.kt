package com.mei.picker.ui

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
import com.mei.picker.loader.PhotoAlbumLoader
import com.mei.picker.model.PhotoAlbum
import com.mei.picker.model.PhotoItem
import com.mei.picker.tokePhoto
import com.mei.picker.ui.adapter.FolderPickerAdapter
import com.mei.picker.ui.adapter.ImagePickerAdapter
import kotlinx.android.synthetic.main.picker_layout_activity.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */


internal class ImagePickerActivity : AppCompatActivity() {

    private val defaultSelectedList = arrayListOf<String>() // app初始被选择的条目
    private val photoAlbum = arrayListOf<PhotoAlbum>()
    private val photoList = arrayListOf<PhotoItem?>()
    private val pickerAdapter: ImagePickerAdapter by lazy {
        val adapter = ImagePickerAdapter(photoList) { position, item ->
            if (position == 0 && item == null) {
                tokePhoto {
                    if (it != null) {
                        putPhoto(PhotoItem().apply {
                            imagePath = it.path
                            imageId = it.path
                        })
                        commit_view.performClick()
                    } else Toast.makeText(this, "拍照失败", Toast.LENGTH_SHORT).show()
                }
            } else if (item != null) {
                // 查看大图
                preViewFragment?.showPreView(photoList.filterNotNull(), position - (photoList.size - photoList.filterNotNull().size))
            }
        }
        adapter
    }
    private val adapterObserver: RecyclerView.AdapterDataObserver by lazy {
        object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                commit_view.text = if (selectedPhotos.size > 0) "使用 (${selectedPhotos.size}/$selectMax)" else "请选择"
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
                commit_view.text = if (selectedPhotos.size > 0) "使用 (${selectedPhotos.size}/$selectMax)" else "请选择"
            }
        }
    }
    private val photoLoader: PhotoAlbumLoader by lazy {
        PhotoAlbumLoader(this) {
            /** 从手机获取到了所有的专辑 **/
            photoAlbum.clear()
            photoList.clear()
            photoAlbum.addAll(it)
            photoList.add(null)
            photoAlbum.forEach { img -> photoList.addAll(img.imageList) }
            photoAlbum.add(0, PhotoAlbum().apply {
                bucketName = "所有图片"
                imageList.addAll(photoList.filterNotNull())
            })
            selectedPhotos.addAll(photoList.filterNotNull().filter { p -> defaultSelectedList.contains(p.imagePath) })
            pickerAdapter.notifyDataSetChanged()
            folderAdapter.notifyDataSetChanged()
            folderPop.height = photoAlbum.size * this.dip(40)
        }
    }
    val folderAdapter: FolderPickerAdapter by lazy {
        FolderPickerAdapter(photoAlbum) {
            // : joker 2019-09-20 换文件夹
            photoList.clear()
            if (selectMax == 1) photoList.add(null)
            photoList.addAll(it.imageList)
            pickerAdapter.notifyDataSetChanged()
            folderPop.dismiss()
            category_btn.text = it.bucketName
        }
    }
    private val folderPop: PopupWindow by lazy { createFolderPopWindow() }

    private val preViewFragment: ImagePreviewFragment? by lazy { supportFragmentManager.findFragmentById(R.id.preview_fragment) as? ImagePreviewFragment }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        selectMax = intent.getIntExtra(PICKER_MAX_LIMIT, selectMax)
        defaultSelectedList.clear()
        defaultSelectedList.addAll(
                intent.getStringArrayListExtra(PICKER_SELECTED_DEFAULT) ?: arrayListOf())
        selectedPhotos.clear()
        setContentView(R.layout.picker_layout_activity)
        supportActionBar?.hide()
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
            preViewFragment?.apply {
                if (selectedPhotos.isNotEmpty()) showPreView(selectedPhotos, 0)
            }
        }
        commit_view.setOnClickListener {
            /** 返回选择的图片 **/
            setResult(Activity.RESULT_OK, Intent().apply {
                val list = arrayListOf<String>()
                selectedPhotos.forEach { list.add(it.imagePath) }
                putStringArrayListExtra(PICKER_RESULT, list)
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