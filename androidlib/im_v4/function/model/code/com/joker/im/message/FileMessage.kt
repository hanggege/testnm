package com.joker.im.message

import com.joker.im.Message
import com.tencent.imsdk.TIMFileElem
import com.tencent.imsdk.TIMMessage
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-14
 *
 * 待支持
 */
class FileMessage : Message {
    constructor(msg: TIMMessage) : super(msg)

    constructor(path: String) : super(TIMMessage().apply {
        addElement(TIMFileElem().apply {
            setPath(path)
            fileName = File(path).name
        })
    })

    override fun getSummary(): String = "[文件]"
    override fun getCopySummary(): String = ""

}