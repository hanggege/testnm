package com.mei.base.weight.richtext

import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.ImageHeaderParser
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.DefaultImageHeaderParser
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.base.weight.richtext.view.DataImageLinearLayout
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream

/**
 *
 * @author Created by Ling on 2019-07-08
 * 富文本的图片处理
 */

/**
 * 处理图片叉掉的点击事件
 *
 * @param view 整个image对应的relativeLayout view
 */
fun RichTextEditor.onImageCloseClick(view: View?) {
    if (!mTransition.isRunning) {
        val item = view?.findViewById<DataImageLinearLayout>(R.id.img_container)
        val imagePath = item?.absolutePath.orEmpty()
        val disappearingImageIndex = allLayout.indexOfChild(view)
        allLayout.removeView(view)
        if (imagePath.isNotEmpty()) {
            imageDeleteListener?.onDeleteImg(imagePath)
        }
        richTextWatcher?.onTextChanged(getTextOutImg())
        (allLayout.getChildAt(0) as? EditText)?.hint = delegate.hint
        mergeEditText(disappearingImageIndex)
    }
}

/**
 * 图片删除的时候，如果上下方都是EditText，则合并处理
 */
private fun RichTextEditor.mergeEditText(disappearingImageIndex: Int) {
    val preView: View? = allLayout.getChildAt(disappearingImageIndex - 1)
    val nextView: View? = allLayout.getChildAt(disappearingImageIndex)
    if (preView != null && preView is EditText && nextView != null && nextView is EditText) {
        Log.d("LeiTest", "合并EditText")
        val str1 = preView.text?.toString().orEmpty()
        val str2 = nextView.text?.toString().orEmpty()
        val mergeText: String
        mergeText = if (str2.isNotEmpty()) {
            str1 + "\n" + str2
        } else {
            str1
        }
        allLayout.layoutTransition = null
        allLayout.removeView(nextView)
        preView.setText(mergeText)
        preView.requestFocus()
        preView.setSelection(str1.length, str1.length)
        allLayout.layoutTransition = mTransition
    }
}

/**
 * 当点击图片或文字时，需要对上次选中的图片做取消点击效果处理，对本次点击的图片做点击效果展示
 */
fun RichTextEditor.onSelectedTagChanged(currView: View?) {
    currView?.let {
        //上一个选中的View不是RelativeLayout，那么当前选中是RelativeLayout时，做选中处理，赋值上次选中，否则不予处理
        if (lastSelectView == null && it is RelativeLayout) {
            try {
                it.findViewById<View>(R.id.img_container)?.isSelected = true
                it.findViewById<View>(R.id.image_close)?.isVisible = true
                lastSelectView = it
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else if (lastSelectView != null) {//上次选中的是图片RelativeLayout
            try {
                lastSelectView!!.findViewById<View>(R.id.img_container)?.isSelected = false
                lastSelectView!!.findViewById<View>(R.id.image_close)?.isVisible = false
            } catch (e: Exception) {
                e.printStackTrace()
            }
            //当前选择和上次选择是同一个图片，或者当前选择不是图片，做取消选择处理
            if (it !is RelativeLayout || it === lastSelectView) {
                lastSelectView = null
            } else {//当前选择是图片，且和上次选择不同
                try {
                    it.findViewById<View>(R.id.img_container)?.isSelected = true
                    it.findViewById<View>(R.id.image_close)?.isVisible = true
                    lastSelectView = it
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
        }
    }
}

/**
 * 根据view的宽度，动态缩放bitmap尺寸
 *
 * @param width view的宽度
 */
internal fun RichTextEditor.getScaledBitmapWidthAndHeight(filePath: String, width: Int): IntArray {
    val options = BitmapFactory.Options()
    options.inJustDecodeBounds = true
    BitmapFactory.decodeFile(filePath, options)
    val sampleSize = if (options.outWidth > width) options.outWidth / width + 1 else 1
    options.inJustDecodeBounds = true
    options.inSampleSize = sampleSize
    BitmapFactory.decodeFile(filePath, options)
    return intArrayOf(options.outWidth, options.outHeight)
}

/**
 * 插入一张图片
 */
internal fun RichTextEditor.insertImage(scaleWidth: Int, scaleHeight: Int, imagePath: String) {
    val lastEditStr = lastFocusEdit?.text?.toString().orEmpty()
    val cursorIndex = lastFocusEdit?.selectionStart ?: -1
    val editStr1 = lastEditStr.substring(0, cursorIndex)
    val lastEditIndex = allLayout.indexOfChild(lastFocusEdit)

    if (lastEditStr.isEmpty() || editStr1.isEmpty()) {
        // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
        addImageViewAtIndex(lastEditIndex, scaleWidth, scaleHeight, imagePath)
        lastFocusEdit?.hint = ""
    } else {
        // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
        lastFocusEdit?.setText(editStr1)
        val editStr2 = lastEditStr.substring(cursorIndex)
        if (allLayout.childCount - 1 == lastEditIndex || editStr2.isNotEmpty()) {
            lastFocusEdit?.minHeight = 0
            addEditTextAtIndex(lastEditIndex + 1, editStr2)
        } else {
            lastFocusEdit?.setSelection(editStr1.length, editStr1.length)
        }

        addImageViewAtIndex(lastEditIndex + 1, scaleWidth, scaleHeight, imagePath)
        if (allLayout.getChildAt(lastEditIndex + 2) is RelativeLayout) {
            addEditTextAtIndex(lastEditIndex + 2, " ")
            lastFocusEdit?.minHeight = 0
        }
    }
    lastFocusEditOption()
}

internal fun RichTextEditor.lastFocusEditOption() {
    lastFocusEdit?.postDelayed({
        lastFocusEdit!!.requestFocus()
        showKeyBoard()
    }, DELAY_DISPLAY_TIME)
}

/**
 * 在特定位置添加ImageView
 * @param scaleWidth 图片按比例压缩之后的宽
 * @param scaleHeight 图片按比例压缩之后的高
 * @param index 位置
 */
private fun RichTextEditor.addImageViewAtIndex(index: Int, scaleWidth: Int, scaleHeight: Int, imagePath: String) {
    val imageLayout = createImageLayout()
    val imageContainer = imageLayout.findViewById<DataImageLinearLayout>(R.id.img_container)
    val progressView = imageLayout.findViewById<ProgressBar>(R.id.progress_bar)
    progressView.isVisible = true
    imageContainer.absolutePath = imagePath

    // 调整imageView的高度
    val layoutWidth = width - delegate.mMarginLeft - delegate.mMarginRight//取实际宽度
    //如果图片删除等问题造成图片获取不到宽高时 scaleWidth <= 0 || scaleHeight <= 0 高度取宽
    val layoutHeight = if (scaleWidth > 0 && scaleHeight > 0)
        (layoutWidth - imageContainer.paddingLeft - imageContainer.paddingRight) * scaleHeight / scaleWidth + imageContainer.paddingTop + imageContainer.paddingBottom
    else {
        layoutWidth - imageContainer.paddingLeft - imageContainer.paddingRight + imageContainer.paddingTop + imageContainer.paddingBottom
    }
    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, layoutHeight)
    if (index == 0) {
        lp.setMargins(delegate.mMarginLeft, delegate.mTextMarginTop, delegate.mMarginLeft, delegate.mImageMarginBottom)
    } else {
        lp.setMargins(delegate.mMarginLeft, delegate.mImageMarginTop, delegate.mMarginLeft, delegate.mImageMarginBottom)
    }
    imageContainer.layoutParams = lp
    displayImg(imagePath, imageContainer, scaleHeight, layoutWidth, progressView)
    allLayout.addView(imageLayout, index)
}

/**
 * 生成图片View
 */
private fun RichTextEditor.createImageLayout(): RelativeLayout {
    val layout = inflater.inflate(R.layout.rich_edit_imageview2, null) as RelativeLayout
    val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    layout.layoutParams = layoutParams
    layout.tag = viewTagIndex++
    val closeView = layout.findViewById<View>(R.id.image_close)
    closeView.tag = layout.tag
    layout.setOnClickListener(imageListener)
    closeView.setOnClickListener(btnListener)
    return layout
}

/**
 * 区分对待不同的图片格式，如gif，用glide加载，并加进度提示
 * 普通图片就用ImageView加载就行，而长图（现判断尺寸是缩放后比屏幕尺寸长两倍以上的图）使用[BigImageView]加载
 */
private fun RichTextEditor.displayImg(imagePath: String, imageContainer: DataImageLinearLayout,
                                      layoutWidth: Int, layoutHeight: Int, progressView: ProgressBar) {
    if (imageTypeIsGif(imagePath)) {
        Glide.with(context).asGif().load(File(imagePath))
                .apply(RequestOptions.centerCropTransform()
                        .override(layoutWidth, layoutHeight)
                        .placeholder(GlideDisPlayDefaultID.default_square_720)
                        .error(GlideDisPlayDefaultID.default_square_720)
                )
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any, target: Target<GifDrawable>, isFirstResource: Boolean): Boolean {
                        progressView.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable, model: Any, target: Target<GifDrawable>, dataSource: DataSource, isFirstResource: Boolean): Boolean {
                        progressView.visibility = View.GONE
                        return false
                    }
                })
                .into(imageContainer.findViewById<View>(R.id.edit_imageView) as ImageView)
    } else {
//        if (imageHeight <= screenHeight * 2) {
        val imageView = imageContainer.findViewById<ImageView>(R.id.edit_imageView)
        imageView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            imageView.glideDisplay(imagePath,
                    GlideDisPlayDefaultID.default_square_720)
            progressView.visibility = View.GONE
        }
//        } else {
        //长图用大图BigImageView来加载
//            imageContainer.removeAllViews()
//            val imageViewAdd = BigImageView(context)
//            val imageLp = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
//            imageViewAdd.layoutParams = imageLp
//            imageViewAdd.ssiv.isZoomEnabled = false
//            val file = File(imagePath)
//            imageViewAdd.onCacheHit(ImageInfoExtractor.getImageType(file), file)
//            imageContainer.addView(imageViewAdd)
//            progressView.visibility = View.GONE
//    }
    }
}

/**
 * 判断图片文件是否是动图
 *
 * @param uri_str 图片文件的本地地址
 * @return 是否是动图
 */
private fun imageTypeIsGif(uri_str: String): Boolean {
    var inputStream: InputStream? = null
    var type: ImageHeaderParser.ImageType = ImageHeaderParser.ImageType.UNKNOWN
    try {
        inputStream = FileInputStream(uri_str)
        val imageHeaderParser = DefaultImageHeaderParser()
        type = imageHeaderParser.getType(inputStream)
    } catch (e: IOException) {
        e.printStackTrace()
    } finally {
        try {
            inputStream?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }
    return type == ImageHeaderParser.ImageType.GIF
}
