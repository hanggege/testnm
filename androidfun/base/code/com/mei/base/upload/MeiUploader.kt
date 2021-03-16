package com.mei.base.upload

import android.util.Log
import com.mei.orc.callback.TaskCallback
import com.mei.orc.common.CommonConstant
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.upload.Photo
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.util.image.compressImageFileAsync
import com.mei.orc.util.image.uriToFile
import com.net.model.config.UPLOAD_uptoken
import com.net.network.config.UPLOAD_uptoken_Request
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

/**
 * Created by guoyufeng on 6/5/15.
 */
class MeiUploader(private val melkorSpiceManager: RetrofitClient, processUploadVideo: (key: String, percent: Double) -> Unit = { _, _ -> }) {
    companion object {
        private val TAG = MeiUploader::class.java.simpleName
        private const val MAX_RETRY_UPLOAD_COUNT = 5

    }

    private val avatarOptions = UploadOptions(null, null, false, { key, percent -> processUploadVideo(key, percent) }, null)// 优化 UploadOptions
    private val uploadManager = UploadManager()

    /**
     * 上传图片
     */
    fun uploadImage(fileUris: List<String>, finishUploadCallback: TaskCallback<PhotoList>) {
        compressImageFileAsync(uriToFile(fileUris)) { files ->
            if (files != null && files.isNotEmpty() && files[0].length() < 5 * 1024 * 1024)
                uploadCompressedFiles(files, fileUris, finishUploadCallback)
            else {
//                UIToast.toast(Cxt.get(), "图片太大，请换一张图片")
                finishUploadCallback.onFailed(null, CommonConstant.ErrCode.IMAGE_TOO_BIG_FAILED)
            }
        }
    }

    /**
     * 上传视频
     */
    fun uploadVideo(files: List<File>, localUris: List<String>, finishUploadCallback: TaskCallback<PhotoList>) {
        val getTokenCallback = object : TaskCallback<UPLOAD_uptoken>() {
            override fun onSuccess(UPLOAD_uptoken: UPLOAD_uptoken) {
                uploadWithToken(UPLOAD_uptoken, files, localUris, finishUploadCallback)
            }

            override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any) {
                finishUploadCallback.onFailed(exception, errCode, *objects)
            }
        }
        requestToken(getTokenCallback)
    }

    /**
     * 上传log文件
     */
    fun uploadLogFile(UPLOAD_uptoken: UPLOAD_uptoken, files: List<File>, localUris: List<String>, finishUploadCallback: TaskCallback<PhotoList>) {
        retryUploadAll(bindInfoLogAndFile(UPLOAD_uptoken, files, localUris), arrayOfNulls(files.size), 0, finishUploadCallback)
    }

    private fun uploadCompressedFiles(compressedFiles: List<File>, localUris: List<String>, finishUploadCallback: TaskCallback<PhotoList>) {
        val getTokenCallback = object : TaskCallback<UPLOAD_uptoken>() {
            override fun onSuccess(UPLOAD_uptoken: UPLOAD_uptoken) {
                uploadWithToken(UPLOAD_uptoken, compressedFiles, localUris, finishUploadCallback)
            }

            override fun onFailed(exception: Exception?, errCode: Int, vararg objects: Any) {
                finishUploadCallback.onFailed(exception, errCode, *objects)
            }
        }
        requestToken(getTokenCallback)
    }

    private fun requestToken(getTokenCallback: TaskCallback<UPLOAD_uptoken>) {
        melkorSpiceManager.execute(UPLOAD_uptoken_Request(),
                object : RequestListener<UPLOAD_uptoken.Response> {
                    override fun onRequestFailure(e: RxThrowable) {
                        getTokenCallback.onFailed(e, CommonConstant.ErrCode.SPICE_ERROR)// dt 根据异常决定是否要进行重试
                    }

                    override fun onRequestSuccess(response: UPLOAD_uptoken.Response) {
                        if (response.isSuccess) {
                            getTokenCallback.onSuccess(response.data)
                        } else {
                            getTokenCallback.onFailed(null, response.getRtn(), response)
                        }
                    }
                })
    }

    private fun uploadWithToken(upload_uptoken: UPLOAD_uptoken, files: List<File>, localUris: List<String>, callback: TaskCallback<PhotoList>) {
        val infoArray = bindInfoAndFile(upload_uptoken, files, localUris)
        retryUploadAll(infoArray, arrayOfNulls(files.size), 0, callback)
    }

    /**
     * 日志上传
     */
    private fun bindInfoLogAndFile(upload_uptoken: UPLOAD_uptoken, files: List<File>, localUris: List<String>): ConcurrentHashMap<Int, UPLOAD_uptoken> {
        val result = ConcurrentHashMap<Int, UPLOAD_uptoken>()
        for (i in files.indices.reversed()) {
            result[i] = UPLOAD_uptoken().apply {
                upToken = upload_uptoken.upToken
                dir = upload_uptoken.dir
                url = upload_uptoken.url
                localFilePath = localUris[i]
            }
        }
        return result
    }

    private fun bindInfoAndFile(upload_uptoken: UPLOAD_uptoken, files: List<File>, localUris: List<String>): ConcurrentHashMap<Int, UPLOAD_uptoken> {
        val result = ConcurrentHashMap<Int, UPLOAD_uptoken>()
        for (i in files.indices.reversed()) {
            result[i] = UPLOAD_uptoken().apply {
                upToken = upload_uptoken.upToken
                dir = upload_uptoken.dir + files[i].name
                url = upload_uptoken.url + files[i].name
                localFilePath = localUris[i]
            }
        }
        return result
    }

    private fun retryUploadAll(filesToUpload: ConcurrentHashMap<Int, UPLOAD_uptoken>,
                               finishedUpload: Array<Photo?>, retryCount: Int, callback: TaskCallback<PhotoList>) {
        val unfinishedData = ConcurrentHashMap(filesToUpload)
        val counter = AtomicInteger(unfinishedData.size)

        for ((index, info) in filesToUpload) {
            uploadManager.put(info.localFilePath, info.dir, info.upToken, { s, responseInfo, jsonObject ->
                if (responseInfo.isOK) {
                    Log.d(TAG, "upload success with retry[" + retryCount + "], path: " + info.dir + ", url:" +
                            info.url + ", s: " + s + ", responseInfo: " + responseInfo + ", json: " + jsonObject)
                    unfinishedData.remove(index)
                    val data = jsonObject.optJSONObject("data")
                    val id = data?.optInt("photo_id") ?: 0
                    val url = info.url
                    finishedUpload[index] = Photo(id, url, info.localFilePath)
                } else {
                    Log.d(TAG, "upload failed with retry[" + retryCount + "], path: " + info.url + ", url:" +
                            info.url + ", s: " + s + ", responseInfo: " + responseInfo + ", json\n" +
                            "            info.localPath = localUris[i]: " + jsonObject)
                }
                if (counter.decrementAndGet() == 0) {
                    when {
                        unfinishedData.size == 0 -> {
                            callback.onSuccess(PhotoList(finishedUpload))
                        }
                        retryCount <= MAX_RETRY_UPLOAD_COUNT -> {
                            retryUploadAll(unfinishedData, finishedUpload, retryCount + 1, callback)// 网络判断
                        }
                        else -> {
                            callback.onFailed(null, CommonConstant.ErrCode.UPLOAD_FAILED, retryCount)
                        }
                    }
                }
            }, avatarOptions)
        }

    }


}
