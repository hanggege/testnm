package com.mei.video

import androidx.fragment.app.FragmentActivity
import com.mei.video.bridge.takeVideo
import com.mei.video.bridge.videoPickerBridge
import com.mei.video.model.VideoMediaEntity
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-19
 */

/**
 * 选择图片
 * @param maxLimit 最大可选择的数量
 * @param defaultSelected 自动进行选择的图片列表
 * @param back 回调选择完的图片
 */
@JvmOverloads
fun FragmentActivity.pickerVideo(maxLimit: Int = 9,
                                 defaultSelected: List<String> = arrayListOf(),
                                 back: (List<VideoMediaEntity>) -> Unit) {
    videoPickerBridge(maxLimit, defaultSelected, back)
}


/**
 * 调起相机拍照
 */
fun FragmentActivity.tokeVideo(back: (File?) -> Unit) {
    takeVideo(back)
}


