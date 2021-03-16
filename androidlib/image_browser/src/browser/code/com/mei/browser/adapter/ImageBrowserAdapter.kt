package com.mei.browser.adapter

import android.graphics.Bitmap
import android.graphics.PointF
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.ImageViewState
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.github.chrisbanes.photoview.PhotoView
import com.mei.browser.BrowserInfo
import com.mei.picker.R

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/27
 */
internal class ImageBrowserAdapter(val info: BrowserInfo, val itemClick: () -> Unit) : RecyclerView.Adapter<ImageBrowserHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageBrowserHolder {
        return ImageBrowserHolder(LayoutInflater.from(parent.context).inflate(R.layout.image_browser_item, parent, false), itemClick)
    }

    override fun getItemCount(): Int = info.images.size

    override fun onBindViewHolder(holder: ImageBrowserHolder, position: Int) {
        holder.refresh(info, info.images[position])
    }
}

internal class ImageBrowserHolder(itemView: View, val itemClick: () -> Unit) : RecyclerView.ViewHolder(itemView) {
    private val defaultBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ALPHA_8)
    fun refresh(info: BrowserInfo, url: String) {
        itemView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        val photoView = itemView.findViewById<PhotoView>(R.id.browser_img)
        val loadingView = itemView.findViewById<View>(R.id.progress)
        val longImgView = itemView.findViewById<SubsamplingScaleImageView>(R.id.browser_long_img)
        photoView.setOnPhotoTapListener { _, _, _ -> itemClick() }
        longImgView.setOnClickListener { itemClick() }
        loadingView.visibility = View.VISIBLE
        photoView.isZoomable = info.zoomable
        photoView.setImageResource(R.drawable.default_error)
        longImgView.isZoomEnabled = info.zoomable
        longImgView.setImage(ImageSource.bitmap(defaultBitmap))
        Glide.with(photoView)
                .asBitmap()
                .load(url)
                .placeholder(R.drawable.default_error)
                .error(R.drawable.default_error)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onLoadFailed(errorDrawable: Drawable?) {
                        super.onLoadFailed(errorDrawable)
                        longImgView.visibility = View.GONE
                        loadingView.visibility = View.GONE
                        photoView.visibility = View.VISIBLE
                        photoView.setImageDrawable(errorDrawable)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                        photoView.visibility = View.VISIBLE
                        photoView.setImageDrawable(placeholder)
                        longImgView.setImage(ImageSource.bitmap(defaultBitmap))
                    }

                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                        //判断长图
                        loadingView.visibility = View.GONE
                        if (bitmap.height / bitmap.width > 2) {
                            longImgView.visibility = View.VISIBLE
                            photoView.visibility = View.GONE
                            longImgView.minScale = 1f
                            longImgView.maxScale = 3f
                            longImgView.setMinimumScaleType(SubsamplingScaleImageView.SCALE_TYPE_CENTER_CROP)
                            longImgView.setImage(ImageSource.cachedBitmap(bitmap), ImageViewState(1f, PointF(0f, 0f), SubsamplingScaleImageView.ORIENTATION_0))
                        } else {
                            longImgView.visibility = View.GONE
                            photoView.visibility = View.VISIBLE
//                            photoView.setImageBitmap(bitmap)
                            /** 这里重新加载是为了如果是GIF图能动起来 **/
                            Glide.with(photoView)
                                    .load(url)
                                    .placeholder(R.drawable.default_error)
                                    .error(R.drawable.default_error)
                                    .into(photoView)
                        }
                    }

                })
    }

}