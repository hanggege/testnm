package com.mei.im.videorecoder.preview

import android.content.Context
import android.graphics.SurfaceTexture
import android.view.Surface
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.mei.wood.R

/**
 *  Created by zzw on 2019-07-05
 *  Des:
 */

class TextureViewPreview(val context: Context, val parent: ViewGroup) : PreviewImpl() {

    private val mTextureView: TextureView by lazy {
        View.inflate(context, R.layout.texture_view, parent).findViewById<TextureView>(R.id.texture_view).apply {
            surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                    setSize(width, height)
                    onSurfaceChanged()
                }

                override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
                    setSize(width, height)
                }

                override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                    setSize(0, 0)
                    return true
                }

                override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
            }
        }
    }

    override fun getView(): View = mTextureView

    override fun getSurface(): Surface = Surface(mTextureView.surfaceTexture)

    override fun isReady(): Boolean = mTextureView.surfaceTexture != null

}