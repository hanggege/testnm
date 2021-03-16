package com.mei.widget.round

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import com.mei.orc.R


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/28
 *  app:radius_all="20dp"   所有的圆角 以下为上下左右四个角
 *  app:radius_left_bottom="50dp"
 *  app:radius_left_top="50dp"
 *  app:radius_right_bottom="50dp"
 *  app:radius_right_top="50dp"
 *  app:round_stroke_color="#f00" 边框颜色
 *  app:round_stroke_width="4dp" 边框宽度
 */
class RoundImageView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : AppCompatImageView(context, attrs, defStyleAttr) {

    var viewStrokeColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            strokePaint.color = value
            invalidate()
        }
    var viewColor: Int = Color.TRANSPARENT
        set(value) {
            field = value
            viewPaint.color = value
            invalidate()
        }
    var viewStrokeWidth: Float = 0f
        set(value) {
            field = value
            strokePaint.strokeWidth = value
            invalidate()
        }

    var radius: Float = 0f
        set(value) {
            field = value
            radiusLeftTop = value
            radiusLeftBottom = value
            radiusRightTop = value
            radiusRightBottom = value
            invalidate()
        }
    var radiusLeftTop: Float = 0f
    var radiusLeftBottom: Float = 0f
    var radiusRightTop: Float = 0f
    var radiusRightBottom: Float = 0f

    private var bitmapShader: BitmapShader? = null
    private var bitmapPaint: Paint? = null
    val viewPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }
    val strokePaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
        }
    }
    private var drawRectF: RectF = RectF(0f, 0f, 0f, 0f)

    init {
        @SuppressLint("CustomViewStyleable")
        val array = context.obtainStyledAttributes(attrs, R.styleable.RoundImageView)
        radius = array.getDimension(R.styleable.RoundImageView_radius_all, 0f)
        radiusLeftTop = array.getDimension(R.styleable.RoundImageView_radius_left_top, radius)
        radiusLeftBottom = array.getDimension(R.styleable.RoundImageView_radius_left_bottom, radius)
        radiusRightTop = array.getDimension(R.styleable.RoundImageView_radius_right_top, radius)
        radiusRightBottom = array.getDimension(R.styleable.RoundImageView_radius_right_bottom, radius)
        viewStrokeWidth = array.getDimension(R.styleable.RoundImageView_round_stroke_width, 0f)
        viewStrokeColor = array.getColor(R.styleable.RoundImageView_round_stroke_color, Color.TRANSPARENT)
        viewColor = array.getColor(R.styleable.RoundImageView_round_color, Color.TRANSPARENT)
        array.recycle()

        strokePaint.apply {
            color = viewStrokeColor
            strokeWidth = viewStrokeWidth
        }
        viewPaint.apply {
            color = viewColor
        }

    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        setUp()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setUp()
    }

    override fun setPadding(left: Int, top: Int, right: Int, bottom: Int) {
        super.setPadding(left, top, right, bottom)
        setUp()
    }


    @SuppressLint("DrawAllocation")
    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    override fun onDraw(canvas: Canvas) {
        /**有圆角时绘制绘制整个图片的背景区域*/
        if (setOf(radiusLeftTop, radiusLeftBottom, radiusRightTop, radiusRightBottom).size == 1) {
            canvas.drawRoundRect(RectF(drawRectF.left,
                    drawRectF.top,
                    drawRectF.right,
                    drawRectF.bottom),
                    radiusLeftTop, radiusLeftTop, viewPaint)
        }
        if (setOf(radiusLeftTop, radiusLeftBottom, radiusRightTop, radiusRightBottom).size == 1 && radiusLeftTop == 0f) {
            super.onDraw(canvas)
        } else if (setOf(radiusLeftTop, radiusLeftBottom, radiusRightTop, radiusRightBottom).size == 1) {
            /** 角度一样时 **/
            if (bitmapShader != null) bitmapPaint?.let {
                canvas.drawRoundRect(RectF(
                        drawRectF.left + strokePaint.strokeWidth / 2,
                        drawRectF.top + strokePaint.strokeWidth / 2,
                        drawRectF.right - strokePaint.strokeWidth / 2,
                        drawRectF.bottom - strokePaint.strokeWidth / 2),
                        radiusLeftTop, radiusLeftTop, it)
            }
        } else {
            if (bitmapShader != null) bitmapPaint?.let { canvas.drawPath(createClipPath(), it) }
        }

        /** 只有圆角角度一样时才好绘制边框 **/
        if (strokePaint.strokeWidth > 0 && setOf(radiusLeftTop, radiusLeftBottom, radiusRightTop, radiusRightBottom).size == 1) {
            val strokeWidthHalf = strokePaint.strokeWidth / 2
            canvas.drawRoundRect(RectF(drawRectF.left + strokeWidthHalf,
                    drawRectF.top + strokeWidthHalf,
                    drawRectF.right - strokeWidthHalf,
                    drawRectF.bottom - strokeWidthHalf),
                    radiusLeftTop, radiusLeftTop, strokePaint)
        }


    }

    /**
     * 获取图片的边缘path
     */
    private fun createClipPath(): Path {
        val path = Path()
        /**  起点为左上角 **/
        path.moveTo(drawRectF.left, drawRectF.top + radiusLeftTop)
        /**  画左上角的圆角 **/
        path.arcTo(RectF(drawRectF.left, drawRectF.top, radiusLeftTop * 2, radiusLeftTop * 2), 180f, 90f)
        /**  顶部横线 **/
        path.lineTo(drawRectF.right - radiusRightTop, drawRectF.top)
        /**  右上角的圆角 **/
        path.arcTo(RectF(drawRectF.right - 2 * radiusRightTop, drawRectF.top, drawRectF.right, radiusRightTop * 2), 270f, 90f)
        /**  右边线 **/
        path.lineTo(drawRectF.right, drawRectF.bottom - radiusRightBottom)
        /**  右下角 **/
        path.arcTo(RectF(drawRectF.right - 2 * radiusRightBottom, drawRectF.bottom - 2 * radiusRightBottom, drawRectF.right, drawRectF.bottom), 0f, 90f)
        /**  底连线 **/
        path.lineTo(drawRectF.right - radiusLeftBottom, drawRectF.bottom)
        /**  左下角 **/
        path.arcTo(RectF(drawRectF.left, drawRectF.bottom - 2 * radiusLeftBottom, 2 * radiusLeftBottom, drawRectF.bottom), 90f, 90f)
        /**  封口 **/
        path.close()
        return path
    }


    private fun setUp() {
        drawRectF = RectF().apply {
            left = paddingLeft.toFloat()
            top = paddingTop.toFloat()
            right = measuredWidth - paddingRight.toFloat()
            bottom = measuredHeight - paddingBottom.toFloat()
        }
        getBitmapFromDrawable()?.let { bitmap ->
            bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

            //1、根据Bitmap要裁剪的类型，以及bitmap和view的宽高，计算Bitmap需要缩放的比例
            val scale = calculateBitmapScale(bitmap)
            //2、为BitmapShader定义一个变换矩阵Matrix，通过Matrix对Bitmap进行缩放
            val scaleMatrix = Matrix()
            scaleMatrix.setScale(scale, scale)
            //3、通过Matrix将缩放后的Bitmap移动到View的中心位置
            val dx = drawRectF.width() - bitmap.width * scale
            val dy = drawRectF.height() - bitmap.height * scale
            scaleMatrix.postTranslate(dx / 2, dy / 2)//注意只能用一个set方法，其他的要用post或pre方法

            bitmapShader?.setLocalMatrix(scaleMatrix)
            if (bitmapPaint == null)
                bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                    isAntiAlias = true
                    color = Color.WHITE
                }
            bitmapPaint?.shader = bitmapShader
        }
        invalidate()

    }


    /**计算Bitmap需要缩放的比例*/
    private fun calculateBitmapScale(bitmap: Bitmap): Float {
        var scale = 1.0f
        // 如果Bitmap的宽或者高与view的宽高不匹配，计算出需要缩放的比例；缩放后的Bitmap的宽高，一定要【大于】view的宽高
        if (bitmap.width != drawRectF.width().toInt() || bitmap.height != drawRectF.height().toInt()) {
            val scaleWidth = drawRectF.width() * 1.0f / bitmap.width
            val scaleHeight = drawRectF.height() * 1.0f / bitmap.height
            scale = Math.max(scaleWidth, scaleHeight)
        }
        return scale
    }


    /** 获取图片的Bitmap **/
    private fun getBitmapFromDrawable(): Bitmap? {
        val displayDrawable = drawable ?: return null
        if (displayDrawable is BitmapDrawable) {
            return displayDrawable.bitmap
        }
        return try {
            val bitmap: Bitmap = if (displayDrawable is ColorDrawable) {
                Bitmap.createBitmap(2, 2, Bitmap.Config.ARGB_8888)
            } else {
                Bitmap.createBitmap(displayDrawable.intrinsicWidth, displayDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
            }
            val canvas = Canvas(bitmap)
            displayDrawable.setBounds(0, 0, canvas.width, canvas.height)
            displayDrawable.draw(canvas)
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }

    }


}


