package com.mei.im.videorecoder.preview

import android.content.Context
import android.view.*
import androidx.core.view.ViewCompat
import com.mei.wood.R

/**
 *  Created by zzw on 2019-07-05
 *  Des:
 */

class SurfaceViewPreview(val context: Context, val parent: ViewGroup) : PreviewImpl() {

    private val mSurfaceView: SurfaceView by lazy {
        View.inflate(context, R.layout.surface_view, parent).findViewById<SurfaceView>(R.id.surface_view).apply {
            holder.addCallback(object : SurfaceHolder.Callback {
                override fun surfaceChanged(h: SurfaceHolder, format: Int, width: Int, height: Int) {
                    setSize(width, height)
                    if (!ViewCompat.isInLayout(mSurfaceView)) {
                        onSurfaceChanged()
                    }
                }

                override fun surfaceDestroyed(p0: SurfaceHolder?) {
                    setSize(0, 0)
                }

                override fun surfaceCreated(p0: SurfaceHolder?) {
                }
            })
        }
    }

    override fun getView() = mSurfaceView

    override fun getSurface(): Surface {
        return mSurfaceView.holder.surface
    }

    override fun getSurfaceHolder(): SurfaceHolder? {
        return mSurfaceView.holder
    }

    override fun isReady(): Boolean = mWidth != 0 && mHeight != 0

}
