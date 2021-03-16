package com.mei.orc.imageload.cache

import android.annotation.SuppressLint
import com.mei.orc.util.http.requestGet
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.mei.orc.util.save.getStringMMKV
import com.mei.orc.util.save.putMMKV

/**
 * Created by joker on 16/7/6.
 * 图片状态保存
 */

/**
 * 获取图片宽高
 * & 预加载图片
 * @param url
 * @param callback
 */
@SuppressLint("StaticFieldLeak")
fun String.getImageWH(callback: (QiniuImageInfo?) -> Unit = {}) {
    val qiniuImageInfo = getStringMMKV("").json2Obj<QiniuImageInfo>()
    if (QiniuImageInfo.checkWH(qiniuImageInfo)) {
        callback(qiniuImageInfo)
        return
    }
    val and = if (contains("?")) "&" else "?"
    val infoUrl = "$this${and}imageInfo"
    requestGet(infoUrl, null, success = { s ->
        val info = s.json2Obj<QiniuImageInfo>()?.apply {
            netUrl = this@getImageWH
            putMMKV(toJson())
        }
        callback(info)
    }, failure = {
        callback(null)
    })
}
