package com.mei.im.videorecoder.preview

import android.view.Surface
import android.view.SurfaceHolder
import android.view.View

/**
 *  Created by zzw on 2019-07-05
 *  Des:
 */

abstract class PreviewImpl {

    var mWidth: Int = 0

    var mHeight: Int = 0

    var onSurfaceChanged: () -> Unit = {}

    fun setSize(width: Int, height: Int) {
        mWidth = width
        mHeight = height
    }

    abstract fun isReady(): Boolean

    abstract fun getView(): View

    abstract fun getSurface(): Surface

    open fun getSurfaceHolder(): SurfaceHolder? = null

}