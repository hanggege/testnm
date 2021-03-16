package com.mei.base.upload

import com.mei.orc.common.CommonConstant
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.image.compressImageFileAsync
import com.net.model.chick.upload.UploadToken
import com.net.network.chick.upload.UploadTokenRequest
import com.qiniu.android.storage.UploadManager
import com.qiniu.android.storage.UploadOptions
import java.io.File

/**
 * Created by guoyufeng on 6/5/15.
 */

private val uploadManager = UploadManager()
private val avatarOptions = UploadOptions(null, "image/*", false, null, null)// 优化 UploadOptions

fun RetrofitClient.chickUploadAvatar(path: String, userId: Int = 0, session_id: String = "", success: (UploadToken) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    getAvatarToken(userId = userId, session_id = session_id, suffix = path.getFileSuffix(), success = {
        compressFile(it, path, success, failed)
    }, failed = {
        failed(it)
    })
}

private fun String.getFileSuffix(): String {
    val index = lastIndexOf('.')
    if (index > 0) {
        return substring(index + 1)
    }
    return ""
}

private fun compressFile(upload_avatar: UploadToken, path: String, success: (UploadToken) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    compressImageFileAsync(arrayListOf(File(path)), callback = { list ->
        if (list.isNullOrEmpty()) {
            failed(CommonConstant.ErrCode.UPLOAD_FAILED)
        } else {
            val compressedAvatar = list[0]
            uploadManager.put(compressedAvatar, upload_avatar.key, upload_avatar.upToken, { _, responseInfo, _ ->
                if (responseInfo.isOK) {
                    success(upload_avatar)
                } else {
                    failed(CommonConstant.ErrCode.UPLOAD_FAILED)
                }
            }, avatarOptions)
        }
    })
}


private fun RetrofitClient.getAvatarToken(userId: Int = 0, session_id: String = "", suffix: String = "", success: (UploadToken) -> Unit = {}, failed: (code: Int) -> Unit = {}) {
    execute(UploadTokenRequest(
            if (userId > 0) userId else JohnUser.getSharedUser().userID,
            if (session_id.isNotEmpty()) session_id else JohnUser.getSharedUser().sessionID,
            suffix),
            object : RequestListener<UploadToken.Response> {
                override fun onRequestFailure(e: RxThrowable) {
                    failed(CommonConstant.ErrCode.SPICE_ERROR)
                }

                override fun onRequestSuccess(response: UploadToken.Response) {
                    if (response.isSuccess) {
                        success(response.data)
                    } else {
                        failed(response.getRtn())
                    }
                }
            })
}
