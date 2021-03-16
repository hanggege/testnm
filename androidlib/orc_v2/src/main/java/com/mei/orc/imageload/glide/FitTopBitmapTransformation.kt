package com.mei.orc.imageload.glide

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import com.bumptech.glide.load.Key
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.TransformationUtils
import java.security.MessageDigest
import kotlin.math.roundToInt

/**
 *
 * @author Created by lenna on 2020/8/12
 */
class FitTopBitmapTransformation : BitmapTransformation() {
    private val id = "com.mei.orc.imageload.glide.FitTopCenterCircle"
    private val idBytes = id.toByteArray(Key.CHARSET)
    private val paintFlags = Paint.DITHER_FLAG or Paint.FILTER_BITMAP_FLAG
    private val defPaint = Paint(paintFlags)
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(idBytes)
    }

    override fun equals(other: Any?): Boolean {
        return other is FitTopBitmapTransformation
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return fitTop(pool, toTransform, outWidth, outHeight)
    }

    private fun fitTop(pool: BitmapPool, inBitmap: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        if (inBitmap.width == outWidth && inBitmap.height == outHeight) {
            return inBitmap
        }
        val widthPercentage: Float = outWidth / inBitmap.width.toFloat()


        var targetWidth = (widthPercentage * inBitmap.width).roundToInt()
        var targetHeight = (widthPercentage * inBitmap.width).roundToInt()

        if (inBitmap.width == targetWidth && inBitmap.height == targetHeight) {
            return inBitmap
        }

        targetWidth = ((widthPercentage * inBitmap.width).toInt())
        targetHeight = ((widthPercentage * inBitmap.width).toInt())

        val config = getNonNullConfig(inBitmap)
        val toReuse = pool[targetWidth, targetHeight, config]


        val matrix = Matrix()
        matrix.setScale(widthPercentage, widthPercentage)
        applyMatrix(inBitmap, toReuse, matrix)
        return toReuse

    }

    private fun getNonNullConfig(bitmap: Bitmap): Bitmap.Config? {
        return if (bitmap.config != null) bitmap.config else Bitmap.Config.ARGB_8888
    }

    private fun applyMatrix(
            inBitmap: Bitmap, targetBitmap: Bitmap, matrix: Matrix) {
        //借用glide库中的锁 进行图片绘制的时锁操作
        TransformationUtils.getBitmapDrawableLock().lock()
        try {
            val canvas = Canvas(targetBitmap)
            canvas.drawBitmap(inBitmap, matrix, defPaint)
            canvas.setBitmap(null)
        } finally {
            TransformationUtils.getBitmapDrawableLock().unlock()
        }
    }
}