package com.mei.picker.bridge

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
import com.mei.picker.ui.PICKER_RESULT
import com.mei.picker.ui.pickerIntent
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-20
 */

internal fun FragmentActivity.pickerImageKt(maxLimit: Int = 9,
                                            defaultSelected: List<String> = arrayListOf(),
                                            back: (List<String>) -> Unit) {
    requestPermissionKt(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        if (it) {
            val intent = this.pickerIntent(maxLimit, defaultSelected)
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                back(it.data?.getStringArrayListExtra(PICKER_RESULT) ?: arrayListOf())
            }.launch(intent)
        } else {
            back(defaultSelected)
        }
    }

}

internal fun FragmentActivity.requestPermissionKt(vararg permissions: String, hasAll: (Boolean) -> Unit) {
    registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        hasAll(it.filterNot { it.value }.isEmpty())
    }.launch(permissions)
}


internal fun FragmentActivity.cropImageBridge(oriFile: File, whRatio: Pair<Int, Int> = Pair(1, 1), cropBack: (File?) -> Unit) {
    val imageCropFile = File(this.getSaveDir(), "mei_capture_${System.currentTimeMillis()}.jpg")
    val intent = Intent("com.android.camera.action.CROP").apply {
        putExtra("crop", "true")
        putExtra("aspectX", whRatio.first)    //X方向上的比例
        putExtra("aspectY", whRatio.second)    //Y方向上的比例
//            putExtra("outputX", w)
//            putExtra("outputY", w * whRatio.second / whRatio.first)
        putExtra("scale ", true)  //是否保留比例
//            putExtra("return-data", true) //是否在Intent中返回图片
        putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) //设置输出图片的格式
//            putExtra("noFaceDetection", true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val imgUri = FileProvider.getUriForFile(this@cropImageBridge, "${this@cropImageBridge.packageName}.fileProvider", oriFile)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) //添加这一句表示对目标应用临时授权该Uri所代表的文件
            setDataAndType(imgUri, "image/*")  //设置数据源,必须是由FileProvider创建的ContentUri
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageCropFile)) //设置输出  不需要ContentUri,否则失败
        } else {
            setDataAndType(Uri.fromFile(oriFile), "image/*")
            putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageCropFile))
        }
    }
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK && imageCropFile.exists()) {
            cropBack(imageCropFile)
        } else {
            cropBack(null)
        }
    }.launch(intent)
}


internal fun FragmentActivity.takePhotoBridge(takeBack: (File?) -> Unit) {
    requestPermissionKt(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE) {
        if (it) {
            val imageSaveFile = File(this.getSaveDir(), "mei_capture_${System.currentTimeMillis()}.jpg")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val imgUri = FileProvider.getUriForFile(this@takePhotoBridge, "${this@takePhotoBridge.packageName}.fileProvider", imageSaveFile)
                    putExtra(MediaStore.EXTRA_OUTPUT, imgUri)
                } else {
                    putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(imageSaveFile))
                }
                putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString()) //设置图片保存的格式
            }
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK && imageSaveFile.exists()) {
                    takeBack(imageSaveFile)
                } else {
                    takeBack(null)
                }
            }.launch(intent)
        } else {
            takeBack(null)
        }
    }

}