package com.joker.im.message

import com.joker.im.MESSAGE_CUSTOM_IS_LISTENED
import com.joker.im.Message
import com.joker.im.utils.SaveTYPE
import com.joker.im.utils.getSaveFilePath
import com.tencent.imsdk.TIMCallBack
import com.tencent.imsdk.TIMMessage
import com.tencent.imsdk.TIMSoundElem
import com.tencent.imsdk.TIMValueCallBack
import com.tencent.imsdk.conversation.ProgressInfo
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 */
class VoiceMessage : Message {
    constructor(msg: TIMMessage) : super(msg)

    constructor(path: String, duration: Long) : super(TIMMessage().apply {
        addElement(TIMSoundElem().apply {
            setPath(path)
            setDuration(duration)
        })
    })

    override fun getSummary(): String = "[语音]"

    override fun getCopySummary(): String = ""

    fun getSoundElem(): TIMSoundElem? {
        return (0..timMessage.elementCount.toInt())
                .map { timMessage.getElement(it) }
                .find { it is TIMSoundElem } as? TIMSoundElem
    }

    /**
     * 时长，单位 秒
     */
    fun getDuration(): Long {
        var duration: Long = 0
        getSoundElem()?.let {
            duration = it.duration
        }
        return duration
    }

    /**
     * 获取音频文件
     */
    fun getVoicePath(
            success: (File) -> Unit,
            progress: (Long, Long) -> Unit = { _, _ -> },
            fail: (Int, String) -> Unit = { _, _ -> }
    ) {
        val elem = getSoundElem()
        val customPath = getSoundElem()?.path.orEmpty()
        val savePath = getSaveFilePath(SaveTYPE.VOICE, timMessage.msgId)

        when {
            File(customPath).exists() -> success(File(customPath))
            File(savePath).exists() -> success(File(savePath))
            elem != null -> elem.getSoundToFile(savePath, object : TIMValueCallBack<ProgressInfo> {
                override fun onSuccess(info: ProgressInfo?) {
                    info?.let {
                        progress(it.currentSize, it.totalSize)
                    }
                }

                override fun onError(code: Int, msg: String?) {
                    fail(code, msg ?: "获取音频失败")
                }

            }, object : TIMCallBack {
                override fun onSuccess() {
                    success(File(savePath))
                }

                override fun onError(code: Int, msg: String?) {
                    fail(code, msg ?: "获取音频失败")
                }

            })
            else -> fail(-1, "获取音频失败")
        }
    }


    fun isListened(): Boolean = timMessage.customInt == MESSAGE_CUSTOM_IS_LISTENED

    fun setListened() {
        timMessage.customInt = MESSAGE_CUSTOM_IS_LISTENED
    }
}