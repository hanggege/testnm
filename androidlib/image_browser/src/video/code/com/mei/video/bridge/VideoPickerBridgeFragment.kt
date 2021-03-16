package com.mei.video.bridge

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.mei.base.getSaveDir
import com.mei.picker.bridge.requestPermissionKt
import com.mei.picker.ui.PICKER_RESULT
import com.mei.video.model.VideoMediaEntity
import com.mei.video.ui.pickerVideoIntent
import java.io.File
import java.util.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-20
 */

internal fun FragmentActivity.videoPickerBridge(maxLimit: Int = 9,
                                                defaultSelected: List<String> = arrayListOf(),
                                                back: (List<VideoMediaEntity>) -> Unit) {
    val intent = pickerVideoIntent(maxLimit, defaultSelected)
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val user: ArrayList<VideoMediaEntity> = it.data?.getParcelableArrayListExtra(PICKER_RESULT)
                ?: arrayListOf()
        back(user)
    }.launch(intent)
}


internal fun FragmentActivity.takeVideo(takeBack: (File?) -> Unit) {
    requestPermissionKt(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        if (it) {
            val videoSaveFile = File(this.getSaveDir(), "mei_capture_${System.currentTimeMillis()}.mp4")
            val intent = Intent("android.media.action.VIDEO_CAPTURE").apply {
                addCategory("android.intent.category.DEFAULT");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val imgUri = FileProvider.getUriForFile(this@takeVideo, "${this@takeVideo.packageName}.fileProvider", videoSaveFile)
                    putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
                } else {
                    putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(videoSaveFile))
                }
                putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) //设置图片保存的格式
            }
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && videoSaveFile.exists()) {
                    takeBack(videoSaveFile)
                } else {
                    takeBack(null)
                }
            }.launch(intent)
        } else {
            takeBack(null)
        }
    }
}

