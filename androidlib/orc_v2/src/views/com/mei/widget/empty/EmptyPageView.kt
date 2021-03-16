package com.mei.widget.empty

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.updateLayoutParams
import com.mei.orc.R

/**
 * Created by Ling on 2017/12/14.
 * @描述：最早是在小鹿2.0.1的UI更换，为图片 + 描述 + 按钮（可有可无）的模式
 *<!--空图片的宽和高，以及图片资源-->
 * @attr ref android.R.styleable#EmptyPageView_empty_image_width    图片的宽高，当一者为空时，图片位置不创建，并将位置给到下一个存在的view，默认为165dp
 * @attr ref android.R.styleable#EmptyPageView_empty_image_height   默认为175dp
 * @attr ref android.R.styleable#EmptyPageView_empty_image_resource 图片资源
 * @attr ref android.R.styleable#EmptyPageView_empty_image_top_margin   图片距顶部的距离，没有图片时是下一个图片距顶部的位置，默认为48dp
 *<!--空白提示内容-->
 * @attr ref android.R.styleable#EmptyPageView_empty_text   提示文案，默认为""，不显示
 * @attr ref android.R.styleable#EmptyPageView_empty_text_size  提示文案的字体大小，默认为16dp
 * @attr ref android.R.styleable#EmptyPageView_empty_text_color 提示文案的字体颜色，默认为"#9B9B9B"
 * @attr ref android.R.styleable#EmptyPageView_empty_text_top_margin    提示文案距顶部view的距离，没有图片时，请设置距顶部的位置，默认为20dp
 * <!--按钮展示内容和是否显示-->
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_text   点击按钮的文案，默认为""，不显示
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_text_size  点击按钮的字体大小，默认为14dp
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_text_color 点击按钮的字体颜色，默认为"#FFFFFF"
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_background 点击按钮的背景颜色，默认为"R.drawable.empty_btn_bg"
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_width  按钮宽高，默认为100dp
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_height 默认为35dp
 * @attr ref android.R.styleable#EmptyPageView_empty_btn_top_margin 距离前一个view的顶部距离，默认为45dp
 * <!--整个背景的设置-->
 * @attr ref android.R.styleable#EmptyPageView_empty_main_background    背景色，默认为"#F8F8F8"
 */
open class EmptyPageView : RelativeLayout {

    //空图片的宽和高，以及图片资源
    private var mEmptyImageWidth = dip2px(context, 165)
    private var mEmptyImageHeight = dip2px(context, 175)
    private var mEmptyImageResource: Drawable? = null
    private var mEmptyImageTopMargin = dip2px(context, 48)

    //空白提示内容
    private var mEmptyText: CharSequence = ""
    private var mEmptyTextSize = dip2px(context, 16)
    private var mEmptyTextColor: ColorStateList? = null
    private var mEmptyTextTopMargin = dip2px(context, 20)

    //按钮展示内容和是否显示
    private var mEmptyBtnText: CharSequence = ""
    private var mEmptyBtnTextSize = dip2px(context, 14)
    private var mEmptyBtnTextColor: ColorStateList? = null
    private var mEmptyBtnBackground: Drawable? = null
    private var mEmptyBtnWidth = dip2px(context, 100)
    private var mEmptyBtnHeight = dip2px(context, 35)
    private var mEmptyBtnTopMargin = dip2px(context, 45)

    //主背景色
    private var mEmptyMainBackground: Drawable? = null

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        initFromAttributes(context, attrs, defStyleAttr)
        initMainView(context)
        initImageView(context)
        initTextView(context)
        initButton(context)
    }

    /**
     * 设置图片位置和内容
     */
    private fun initImageView(context: Context?) {
        if (mEmptyImageWidth == 0 || mEmptyImageHeight == 0) {
            return
        }
        val emptyImageView = ImageView(context)
        val emptyImageParams = LayoutParams(mEmptyImageWidth, mEmptyImageHeight)
        emptyImageParams.topMargin = mEmptyImageTopMargin
        emptyImageParams.addRule(CENTER_HORIZONTAL)
        emptyImageView.layoutParams = emptyImageParams
        emptyImageView.id = R.id.empty_image
        emptyImageView.setImageDrawable(mEmptyImageResource)
        addView(emptyImageView)
    }

    fun setEmptyImage(@DrawableRes resId: Int): EmptyPageView {
        val imageView: ImageView? = findViewById(R.id.empty_image)
        if (imageView == null) {
            val emptyImageView = ImageView(context)
            val emptyImageParams = if (mEmptyImageWidth == 0 || mEmptyImageHeight == 0) {
                LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            } else {
                LayoutParams(mEmptyImageWidth, mEmptyImageHeight)
            }
            emptyImageParams.topMargin = mEmptyImageTopMargin
            emptyImageParams.addRule(CENTER_HORIZONTAL)
            emptyImageView.layoutParams = emptyImageParams
            emptyImageView.id = R.id.empty_image
            emptyImageView.setImageResource(resId)
            addView(emptyImageView)
            //设置下一个view在图片之下
            val emptyText: TextView? = findViewById(R.id.empty_text)
            val emptyBtn: TextView? = findViewById(R.id.empty_btn)
            if (emptyText != null) {
                val layoutParams = emptyText.layoutParams
                if (layoutParams != null && layoutParams is RelativeLayout.LayoutParams) {
                    layoutParams.addRule(BELOW, R.id.empty_image)
                }
            } else if (emptyBtn != null) {
                val layoutParams = emptyBtn.layoutParams
                if (layoutParams != null && layoutParams is RelativeLayout.LayoutParams) {
                    layoutParams.addRule(BELOW, R.id.empty_image)
                }
            }
        } else {
            imageView.setImageResource(resId)
        }
        return this
    }

    /** 设置图片宽高
     *  @param height dp
     *  @param width dp
     */
    fun setEmptyImageHeightWidth(height: Int, width: Int): EmptyPageView {
        val layoutParams = findViewById<View>(R.id.empty_image)?.layoutParams
        if (height == 0 || width == 0) {
            layoutParams?.height = ViewGroup.LayoutParams.WRAP_CONTENT
            layoutParams?.width = ViewGroup.LayoutParams.WRAP_CONTENT
        } else {
            mEmptyImageHeight = dip2px(context, height)
            mEmptyImageWidth = dip2px(context, width)
            layoutParams?.height = mEmptyImageHeight
            layoutParams?.width = mEmptyImageWidth
        }
        return this
    }

    /**设置图片距顶部间距
     *@param top dp
     */
    fun setEmptyImageMarginTop(top: Int): EmptyPageView {
        mEmptyImageTopMargin = dip2px(context, top)
        findViewById<View>(R.id.empty_image)?.updateLayoutParams<RelativeLayout.LayoutParams> {
            topMargin = mEmptyImageTopMargin
        }
        return this
    }

    /**
     * 设置空白提示内容
     */
    private fun initTextView(context: Context?) {
        if (mEmptyText.isEmpty()) {
            return
        }
        val emptyTextView = TextView(context)
        emptyTextView.text = mEmptyText
        emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyTextSize.toFloat())
        emptyTextView.id = R.id.empty_text
        emptyTextView.setTextColor(mEmptyTextColor)
        val emptyTextParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        if (mEmptyImageWidth != 0 && mEmptyImageHeight != 0) {
            //有图片，设置在图片下方，距离是图片和文字的距离
            emptyTextParams.addRule(BELOW, R.id.empty_image)
        }
        emptyTextParams.topMargin = mEmptyTextTopMargin
        emptyTextParams.addRule(CENTER_HORIZONTAL)
        addView(emptyTextView, emptyTextParams)
    }

    //设置空白文案
    fun setEmptyText(text: String): EmptyPageView {
        var emptyTextView: TextView? = findViewById(R.id.empty_text)
        if (emptyTextView == null) {
            emptyTextView = TextView(context)
            emptyTextView.text = text
            emptyTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyTextSize.toFloat())
            emptyTextView.id = R.id.empty_text
            emptyTextView.setTextColor(mEmptyTextColor)
            val emptyTextParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            if (mEmptyImageWidth != 0 && mEmptyImageHeight != 0) {
                //有图片，设置在图片下方，距离是图片和文字的距离
                emptyTextParams.addRule(BELOW, R.id.empty_image)
            }
            emptyTextParams.topMargin = mEmptyTextTopMargin
            emptyTextParams.addRule(CENTER_HORIZONTAL)
            addView(emptyTextView, emptyTextParams)
            //设置下一个view在空白文案之下
            val layoutParams = findViewById<TextView>(R.id.empty_btn)?.layoutParams
            if (layoutParams != null && layoutParams is RelativeLayout.LayoutParams) {
                layoutParams.addRule(BELOW, R.id.empty_text)
            }
        } else {
            emptyTextView.text = text
        }
        return this
    }

    fun setEmptyTextSize(size: Int): EmptyPageView {
        mEmptyTextSize = dip2px(context, size)
        findViewById<TextView>(R.id.empty_text)?.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyTextSize.toFloat())
        return this
    }

    fun setEmptyTextColor(colorString: String): EmptyPageView {
        mEmptyTextColor = ColorStateList.valueOf(Color.parseColor(colorString))
        findViewById<TextView>(R.id.empty_text)?.setTextColor(mEmptyTextColor)
        return this
    }

    /**设置空白文案距顶部间距
     *@param top dp
     */
    fun setEmptyTopMargin(top: Int): EmptyPageView {
        mEmptyTextTopMargin = dip2px(context, top)
        findViewById<TextView>(R.id.empty_text)?.updateLayoutParams<RelativeLayout.LayoutParams> {
            topMargin = mEmptyImageTopMargin
        }
        return this
    }

    /**设置空白文案距顶部间距
     *@param top dp
     */
    fun setEmptyCenter(): EmptyPageView {
        mEmptyTextTopMargin = dip2px(context, top)
        findViewById<TextView>(R.id.empty_text)?.updateLayoutParams<RelativeLayout.LayoutParams> {
            addRule(ALIGN_PARENT_TOP, R.id.empty_image)
            gravity = Gravity.CENTER

        }
        return this
    }

    /**
     * 设置按钮的内容和是否显示
     */
    private fun initButton(context: Context?) {
        if (mEmptyBtnText.isEmpty()) {
            return
        }
        val clickBtn = TextView(context)
        clickBtn.setTextColor(mEmptyBtnTextColor)
        clickBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyBtnTextSize.toFloat())
        clickBtn.gravity = Gravity.CENTER
        clickBtn.text = mEmptyBtnText
        clickBtn.id = R.id.empty_btn
        clickBtn.background = mEmptyBtnBackground
        val clickBtnParams = LayoutParams(mEmptyBtnWidth, mEmptyBtnHeight)
        if (findViewById<TextView>(R.id.empty_text) != null) {
            //有文字，不管有没有图片  有按钮，设置在文字下方，距离是按钮到文字的距离
            clickBtnParams.addRule(BELOW, R.id.empty_text)
        } else if (findViewById<TextView>(R.id.empty_image) != null) {
            //没有文字，有图片  有按钮，设置在图片下方，距离是按钮到文字的距离
            clickBtnParams.addRule(BELOW, R.id.empty_image)
        }
        clickBtnParams.topMargin = mEmptyBtnTopMargin
        clickBtnParams.addRule(CENTER_HORIZONTAL)
        addView(clickBtn, clickBtnParams)
    }

    fun setBtnText(text: String): EmptyPageView {
        var clickBtn: TextView? = findViewById(R.id.empty_btn)
        if (clickBtn == null) {
            clickBtn = TextView(context)
            clickBtn.setTextColor(mEmptyBtnTextColor)
            clickBtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyBtnTextSize.toFloat())
            clickBtn.gravity = Gravity.CENTER
            clickBtn.text = mEmptyBtnText
            clickBtn.id = R.id.empty_btn
            clickBtn.background = mEmptyBtnBackground
            val clickBtnParams = LayoutParams(mEmptyBtnWidth, mEmptyBtnHeight)
            if (findViewById<TextView>(R.id.empty_text) != null) {
                //有文字，不管有没有图片  有按钮，设置在文字下方，距离是按钮到文字的距离
                clickBtnParams.addRule(BELOW, R.id.empty_text)
            } else if (findViewById<TextView>(R.id.empty_image) != null) {
                //没有文字，有图片  有按钮，设置在图片下方，距离是按钮到文字的距离
                clickBtnParams.addRule(BELOW, R.id.empty_image)
            }
            clickBtnParams.topMargin = mEmptyBtnTopMargin
            clickBtnParams.addRule(CENTER_HORIZONTAL)
            addView(clickBtn, clickBtnParams)
        } else {
            clickBtn.text = text
        }
        return this
    }

    fun setEmptyBtnSize(size: Int): EmptyPageView {
        mEmptyBtnTextSize = dip2px(context, size)
        findViewById<TextView>(R.id.empty_btn)?.setTextSize(TypedValue.COMPLEX_UNIT_PX, mEmptyTextSize.toFloat())
        return this
    }

    fun setEmptyBtnColor(colorString: String): EmptyPageView {
        mEmptyBtnTextColor = ColorStateList.valueOf(Color.parseColor(colorString))
        findViewById<TextView>(R.id.empty_btn)?.setTextColor(mEmptyTextColor)
        return this
    }

    /**设置按钮距顶部间距
     *@param top dp
     */
    fun setEmptyBtnMargin(top: Int): EmptyPageView {
        mEmptyBtnTopMargin = dip2px(context, top)
        findViewById<View>(R.id.empty_btn)?.updateLayoutParams<RelativeLayout.LayoutParams> {
            topMargin = mEmptyImageTopMargin
        }
        return this
    }

    /**
     * 设置按钮的点击事件，前提是要setBtnText(text)之后或者在xml文件中配置EmptyPageView_empty_btn_text
     */
    fun setOnBtnClick(l: OnClickListener): EmptyPageView {
        findViewById<TextView>(R.id.empty_btn)?.setOnClickListener(l)
        return this
    }

    fun setBtnVisible(visible: Boolean): EmptyPageView {
        findViewById<TextView>(R.id.empty_btn)?.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    fun setBtnBackground(@DrawableRes id: Int): EmptyPageView {
        mEmptyBtnBackground = ContextCompat.getDrawable(context, id)
        findViewById<TextView>(R.id.empty_btn)?.background = mEmptyBtnBackground
        return this
    }

    /**
     * 设置主背景的RelativeLayout
     */
    private fun initMainView(@Suppress("UNUSED_PARAMETER") context: Context) {
        background = mEmptyMainBackground
    }

    fun setMainBackground(@DrawableRes id: Int): EmptyPageView {
        mEmptyMainBackground = ContextCompat.getDrawable(context, id)
        background = mEmptyBtnBackground
        return this
    }

    private fun initFromAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val a = context.obtainStyledAttributes(attrs, R.styleable.EmptyPageView, defStyleAttr, 0)
        mEmptyImageWidth = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_image_width, mEmptyImageWidth)
        mEmptyImageHeight = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_image_height, mEmptyImageHeight)
        mEmptyImageResource = a.getDrawable(R.styleable.EmptyPageView_empty_image_resource)
        mEmptyImageTopMargin = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_image_top_margin, mEmptyImageTopMargin)
        mEmptyText = a.getText(R.styleable.EmptyPageView_empty_text) ?: mEmptyText
        mEmptyTextSize = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_text_size, mEmptyTextSize)
        mEmptyTextColor = a.getColorStateList(R.styleable.EmptyPageView_empty_text_color)
        if (mEmptyTextColor == null) {
            mEmptyTextColor = ColorStateList.valueOf(Color.parseColor("#9B9B9B"))
        }
        mEmptyTextTopMargin = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_text_top_margin, mEmptyTextTopMargin)
        mEmptyBtnText = a.getText(R.styleable.EmptyPageView_empty_btn_text) ?: mEmptyBtnText
        mEmptyBtnTextSize = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_btn_text_size, mEmptyBtnTextSize)
        mEmptyBtnTextColor = a.getColorStateList(R.styleable.EmptyPageView_empty_btn_text_color)
        if (mEmptyBtnTextColor == null) {
            mEmptyBtnTextColor = ColorStateList.valueOf(Color.WHITE)
        }
        mEmptyBtnBackground = a.getDrawable(R.styleable.EmptyPageView_empty_btn_background)
        if (mEmptyBtnBackground == null) {
            mEmptyBtnBackground = ContextCompat.getDrawable(context, R.drawable.empty_btn_bg)
        }
        mEmptyMainBackground = a.getDrawable(R.styleable.EmptyPageView_empty_main_background)
        if (mEmptyMainBackground == null) {
            mEmptyMainBackground = ContextCompat.getDrawable(context, R.color.color_f8f8f8)
        }
        mEmptyBtnWidth = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_btn_width, mEmptyBtnWidth)
        mEmptyBtnHeight = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_btn_height, mEmptyBtnHeight)
        mEmptyBtnTopMargin = a.getDimensionPixelSize(R.styleable.EmptyPageView_empty_btn_top_margin, mEmptyBtnTopMargin)
        a.recycle()
    }

    private fun dip2px(context: Context, dipValue: Int): Int {
        val m = context.resources.displayMetrics.density
        return (dipValue * m + 0.5f).toInt()
    }
}