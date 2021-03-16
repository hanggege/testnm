package com.joker.im.message

import com.joker.im.Message
import com.tencent.imsdk.TIMImage
import com.tencent.imsdk.TIMImageElem
import com.tencent.imsdk.TIMImageType
import com.tencent.imsdk.TIMMessage
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */
class ImageMessage : Message {

    constructor(msg: TIMMessage) : super(msg)

    constructor(path: String, isOri: Boolean = false) : super(TIMMessage().apply {
        addElement(TIMImageElem().apply {
            setPath(path)
            level = if (isOri) 0 else 1
        })
    })


    override fun getSummary(): String = "[图片]"

    override fun getCopySummary(): String = ""

    /** 获取图片信息 **/
    fun getImageElem(type: TIMImageType = TIMImageType.Original): TIMImage? {
        val imgList = (timMessage.getElement(0) as? TIMImageElem)?.imageList.orEmpty()
        return imgList.find { it.type == type } ?: imgList.firstOrNull()
    }

    /** 优先取本地，其次取指定type，最后获取原图片 **/
    fun getImageUrl(type: TIMImageType = TIMImageType.Original): String {
        val path = (timMessage.getElement(0) as? TIMImageElem)?.path.orEmpty()
        return when {
            (File(path).exists() && File(path).length() > 0) -> path
            getImageElem(type)?.url.orEmpty().isNotEmpty() -> getImageElem(type)?.url.orEmpty()
            else -> getImageElem()?.url.orEmpty()
        }
    }


}