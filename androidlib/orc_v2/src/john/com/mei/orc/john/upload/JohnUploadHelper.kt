package com.mei.orc.john.upload

import com.mei.orc.common.CommonConstant
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.UPLOAD_avatar
import com.mei.orc.john.network.request.UPLOAD_avatar_Request
import com.mei.orc.util.image.compressImageFileAsync
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import java.io.File

/**
 * Created by guoyufeng on 6/5/15.
 */

private val uploadManager = UploadManager()
private val avatarOptions = UploadOptions(null, "image/*", false, null, null)// 优化 UploadOptions

fun RetrofitClient.upload_avatar(path: String, success: (UPLOAD_avatar) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    getAvatarToken(success = {
        compressFile(it, path, success, failed)
    }, failed = {
        failed(it)
    })
}

private fun compressFile(
        upload_avatar: UPLOAD_avatar,
        path: String,
        success: (UPLOAD_avatar) -> Unit = {},
        failed: (code: Int) -> Unit = {}
) {
    compressImageFileAsync(arrayListOf(File(path)), callback = { list ->
        if (list.isNullOrEmpty()) {
            failed(CommonConstant.ErrCode.UPLOAD_FAILED)
        } else {
            val compressedAvatar = list[0]
            uploadManager.put(compressedAvatar, upload_avatar.key, upload_avatar.uptoken, { _, responseInfo, _ ->
                if (responseInfo.isOK) {
                    success(upload_avatar)
                } else {
                    failed(CommonConstant.ErrCode.UPLOAD_FAILED)
                }
            }, avatarOptions)
        }
    })
}


private fun RetrofitClient.getAvatarToken(success: (UPLOAD_avatar) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    execute(UPLOAD_avatar_Request(),
            object : RequestListener<UPLOAD_avatar.Response> {
                override fun onRequestFailure(e: RxThrowable) {
                    failed(CommonConstant.ErrCode.SPICE_ERROR)
                }

                override fun onRequestSuccess(response: UPLOAD_avatar.Response) {
                    val data = response.data
                    if (response.isSuccess && data != null) {
                        success(data)
                    } else {
                        failed(response.getRtn())
                    }
                }
            })
}
