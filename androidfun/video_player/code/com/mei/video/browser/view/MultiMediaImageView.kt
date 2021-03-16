package com.mei.video.browser.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.widget.FrameLayout
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.video.browser.adapter.MultiMediaData
import com.mei.video.browser.adapter.MultiMediaRefresh
import com.mei.wood.R
import kotlinx.android.synthetic.main.multi_image_item_layout.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/23
 */
class MultiMediaImageView(context: Context) : FrameLayout(context), MultiMediaRefresh {


    init {
        layoutInflaterKtToParentAttach(R.layout.multi_image_item_layout)
    }

    private var defaultBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    var mediaData: MultiMediaData? = null
    var position = 0


    override fun performSelected(position: Int) {
    }

    override fun refresh(position: Int, data: MultiMediaData?) {
        this.position = position
        this.mediaData = data
        multi_browser_progress.isVisible = true
        multi_browser_error_img.isVisible = true
        multi_browser_img.isVisible = false
        multi_browser_img.setImageDrawable(null)
        multi_browser_long_img.isVisible = false
        multi_browser_long_img.setImage(ImageSource.bitmap(defaultBitmap))
        Glide.with(multi_browser_img.context)
                .asBitmap()
                .load(data?.url.orEmpty())
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        multi_browser_progress.isVisible = false
                        multi_browser_error_img.isVisible = true
                        multi_browser_img.isVisible = false
                        multi_browser_long_img.isVisible = false
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        multi_browser_progress.isVisible = false
                        multi_browser_error_img.isVisible = false
                        multi_browser_img.isVisible = true
                        //判断长图
                        if (bitmap.height / bitmap.width > 2) {
                            multi_browser_img.isVisible = false
                            multi_browser_long_img.isVisible = true
                            multi_browser_long_img.minScale = 1f
                            multi_browser_long_img.maxScale = 3f
                            multi_browser_long_img.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            multi_browser_long_img.setImage(ImageSource.cachedBitmap(bitmap), ImageViewState(1f, PointF(0f, 0f), SubsamplingScaleImageView.ORIENTATION_0))
                        } else {
                            multi_browser_img.isVisible = true
                            multi_browser_long_img.isVisible = false
                            /** 这是重新用glide加载是为了兼容GIF图 **/
                            Glide.with(multi_browser_img)
                                    .load(data?.url.orEmpty())
                                    .into(multi_browser_img)
                        }
                    }

                })

    }


}