package com.mei.picker

import androidx.fragment.app.FragmentActivity
import com.mei.picker.bridge.cropImageBridge
import com.mei.picker.bridge.pickerImageKt
import com.mei.picker.bridge.takePhotoBridge
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
fun FragmentActivity.pickerImage(maxLimit: Int = 9,
                                 defaultSelected: List<String> = arrayListOf(),
                                 back: (List<String>) -> Unit) {
    pickerImageKt(maxLimit, defaultSelected, back)
}


/**
 * 进行图片裁剪
 * @param whRatio 宽高比
 */
@JvmOverloads
fun FragmentActivity.cropImage(whRatio: Pair<Int, Int> = Pair(1, 1),
                               back: (String) -> Unit) {
    pickerImage(1) {
        val filePath = it.firstOrNull()
        if (filePath != null) {
            cropImageBridge(File(filePath), whRatio) {
                back(it?.path.orEmpty())
            }
        } else {
            back("")
        }
    }
}

/**
 * 调起相机拍照
 */
fun FragmentActivity.tokePhoto(back: (File?) -> Unit) {
    takePhotoBridge(back)
}

/**
 * 调起相机拍照并裁减
 */
fun FragmentActivity.tokePhotoAndCrop(whRatio: Pair<Int, Int> = Pair(1, 1), back: (String?) -> Unit) {
    takePhotoBridge { file ->
        if (file == null) {
            back(null)
        } else {
            cropImageBridge(file, whRatio) {
                back(it?.path.orEmpty())
            }
        }
    }
}