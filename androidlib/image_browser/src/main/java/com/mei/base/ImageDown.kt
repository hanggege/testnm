package com.mei.base

import android.content.Context
import android.media.MediaScannerConnection
import android.os.AsyncTask
import android.os.Environment
import android.util.Base64
import android.util.Log
import android.widget.Toast
import com.bumptech.glide.Glide
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-24
 */

/**
 * 下载图片到本地相册
 * 现在使用的Q的沙盒模式，不再使用在SD卡上操作
 */
fun Context.saveImgToGallery(imgUrl: String, back: (File?) -> Unit = {}) {
    object : AsyncTask<String, Int, File?>() {
        override fun doInBackground(vararg params: String?): File? {
            Log.e("info", "下载图片到本地: ${params.firstOrNull()}")
            var saveFile: File? = null
            try {
                val url = params.firstOrNull().orEmpty()
                val data = if (url.startsWith("data:image/png;base64,")) {
                    Base64.decode(url.replace("data:image/png;base64,", ""), Base64.DEFAULT)
                } else url

                val cacheFile = Glide.with(this@saveImgToGallery)
                        .download(data)
                        .submit()
                        .get()
                saveFile = File(getSaveDir(), "mei_${cacheFile.name}.jpg")
                //获得原文件流
                val fis = FileInputStream(cacheFile)
                //输出文件流
                val fos = FileOutputStream(saveFile)
                // 缓冲数组
                val b = ByteArray(1024 * 8)
                while (fis.read(b) != -1) {
                    fos.write(b)
                }
                fos.flush()
                fis.close()
                fos.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return saveFile
        }

        override fun onPostExecute(result: File?) {
            super.onPostExecute(result)
            var msg = "保存失败"
            if (result != null) {
                msg = "保存成功"
                //扫描媒体库
                MediaScannerConnection.scanFile(this@saveImgToGallery, arrayOf(result.absolutePath), null, null)
            }
            Toast.makeText(this@saveImgToGallery, msg, Toast.LENGTH_SHORT).show()
            back(result)
        }

    }.execute(imgUrl)
}

@Suppress("DEPRECATION")
internal fun Context.getSaveDir(): File {
    return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            ?: File(filesDir, "mei").apply { mkdirs() }
}