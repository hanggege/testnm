package com.mei.orc.util.image

import android.graphics.Bitmap
import android.util.Log
import com.mei.orc.Cxt
import com.mei.orc.common.CommonConstant
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.imageload.OkHttpImageLoader
import id.zelory.compressor.Compressor
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import io.reactivex.subscribers.ResourceSubscriber
import java.io.File
import java.io.FileOutputStream
import java.util.*


/**
 * Created by dt on 25/2/15.
 */
fun uriToFile(imageUris: List<String>): List<File> {
    val fileList = ArrayList<File>()
    for (uri in imageUris) {
        fileList.add(File(uri))
    }
    return fileList
}


fun compressImageFileAsync(imageFiles: List<File>, callback: (List<File>?) -> Unit = {}) {
    Flowable.create(
            FlowableOnSubscribe<List<File>> { emitter ->
                try {
                    val list = ArrayList<File>()
                    for (file in imageFiles) {
                        try {
                            val resultFile = Compressor(Cxt.get())
                                    .setMaxHeight(Integer.MAX_VALUE)
                                    .setMaxWidth(Integer.MAX_VALUE)
                                    .compressToFile(file)
                            if (resultFile.length() > 0) list.add(resultFile)
                            else list.add(file)
                        } catch (e: Exception) {
                            Log.e("image compress", "subscribe: " + "压缩失败，直接用原图")
                            list.add(file)
                        }
                    }
                    emitter.onNext(list)
                    emitter.onComplete()
                } catch (e: Exception) {
                    e.printStackTrace()
                    emitter.onError(e)
                }
            }, BackpressureStrategy.BUFFER)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : ResourceSubscriber<List<File>>() {
                override fun onNext(files: List<File>) {
                    callback(files)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    callback(null)
                    dispose()
                }

                override fun onComplete() {
                    dispose()
                }
            })
}


fun RetrofitClient.saveUrlImageAsPNG(url: String, file: File, success: (File) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    OkHttpImageLoader.loadImageAsBitmap(this, url) { bitmap ->
        if (bitmap != null) {
            var fos: FileOutputStream? = null
            var exception: Exception? = null
            try {
                fos = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            } catch (e: Exception) {
                e.printStackTrace()
                exception = e
            } finally {
                try {
                    fos?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                if (exception != null) {
                    failed(CommonConstant.ErrCode.SAVE_FILE_FAILED)
                } else {
                    success(file)
                }
            }
        } else {
            failed(CommonConstant.ErrCode.IMAGE_LOAD_FAILED)
        }
    }

}
