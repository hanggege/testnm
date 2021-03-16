package com.mei.im.videorecoder.recode

import com.mei.im.videorecoder.preview.PreviewImpl

/**
 *  Created by zzw on 2019-07-04
 *  Des:
 */

abstract class VideoRecoderImpl(var mCallback: Callback, val mPreviewImpl: PreviewImpl) {

    var isRecoding = false

    abstract fun start(path: String)
    abstract fun prepare(): Boolean
    abstract fun stop()
    abstract fun release()

    interface Callback {
        fun onError(code: Int, msg: String)

        /**
         * 如果不是0表示不是正常退出  part1：-1 在VideoRecoderCamera2里面表示在开始准备阶段停止录制退出
         */
        fun onStop(code: Int = 0)

        fun onStart()
        fun onPrepare()
    }
}

