package com.joker.im.message

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.util.Log
import com.joker.im.Message
import com.joker.im.utils.SaveTYPE
import com.joker.im.utils.getSaveFilePath
import com.joker.im.utils.saveToLocal
import com.tencent.imsdk.*
import com.tencent.imsdk.conversation.ProgressInfo
import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-14
 */
private fun getVideoThumbnail(file: String): Bitmap? {
    val media = MediaMetadataRetriever()
    media.setDataSource(file)
    return media.frameAtTime
}

class VideoMessage : Message {
    constructor(msg: TIMMessage) : super(msg)

    constructor(path: String, duration: Long) : super(TIMMessage().apply {
        addElement(TIMVideoElem().apply {
            val thumb = getVideoThumbnail(path)
            val snapshot = TIMSnapshot()
            snapshot.height = thumb?.height?.toLong() ?: 0
            snapshot.width = thumb?.width?.toLong() ?: 0
            snapshot.type = "PNG"

            val video = TIMVideo()
            video.type = "mp4"
            video.duaration = duration

            setSnapshot(snapshot)
            setVideo(video)
            videoPath = path
            snapshotPath = thumb?.saveToLocal(System.currentTimeMillis().toString())?.path.orEmpty()
        })
    })


    override fun getSummary(): String = "[小视频]"
    override fun getCopySummary(): String = ""


    fun getVideoElem(): TIMVideoElem? {
        return (0..timMessage.elementCount.toInt())
                .map { timMessage.getElement(it) }
                .find { it is TIMVideoElem } as? TIMVideoElem
    }

    /**
     * 时长，单位 秒
     */
    fun getDuration(): Long {
        var duration: Long = 0
        getVideoElem()?.videoInfo?.let {
            duration = it.duaration
        }
        return duration
    }

    fun getSize(): Long {
        var size: Long = 0
        getVideoElem()?.videoInfo?.let {
            size = it.size
        }
        return size
    }

    /**
     * 获取屏幕截图
     */
    fun getSnapshot(back: (File?) -> Unit) {
        val snapshot = getVideoElem()?.snapshotInfo
        val snapshotPath = getVideoElem()?.snapshotPath.orEmpty()
        val path = getSaveFilePath(SaveTYPE.FILE, timMessage.msgUniqueId.toString())
        when {
            File(snapshotPath).exists() -> back(File(snapshotPath))
            File(path).exists() -> back(File(path))
            snapshot != null -> snapshot.getImage(path, null, object : TIMCallBack {
                override fun onSuccess() {
                    back(File(path))
                }

                override fun onError(code: Int, msg: String?) {
                    Log.e("info", ": $msg");
                    back(null)
                }

            })
            else -> back(null)
        }
    }

    /**
     * 获取视频文件
     */
    fun getVideoPath(
            success: (File) -> Unit,
            progress: (Long, Long) -> Unit = { _, _ -> },
            fail: (Int, String) -> Unit = { _, _ -> }
    ) {
        val info = getVideoElem()?.videoInfo
        val customPath = getVideoElem()?.videoPath.orEmpty()
        val savePath = getSaveFilePath(SaveTYPE.VIDEO, timMessage.msgId)
        when {
            File(customPath).exists() -> success(File(customPath))
            File(savePath).exists() -> success(File(savePath))
            info != null -> {
                info.getVideo(savePath, object : TIMValueCallBack<ProgressInfo> {
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
            }
            else -> fail(-1, "获取视频失败")
        }

    }


}