package com.mei.base.common

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKtToParentAttach
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_empty_page.view.*

/**
 * Created by hang on 2020/7/10.
 */
class EmptyPageLayout @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        LinearLayout(context, attrs, defStyleAttr) {


    init {
        layoutInflaterKtToParentAttach(R.layout.view_empty_page)

        val attributes = context.obtainStyledAttributes(attrs, R.styleable.EmptyPageLayout, defStyleAttr, 0)
        val emptyImage = attributes.getDrawable(R.styleable.EmptyPageLayout_empty_layout_image_resource)
                ?: ContextCompat.getDrawable(context, R.drawable.home_error_page)
        val emptyText = attributes.getText(R.styleable.EmptyPageLayout_empty_layout_text)
                ?: context.getString(R.string.no_network)
        val emptyBtnText = attributes.getText(R.styleable.EmptyPageLayout_empty_layout_btn_text)
                ?: context.getString(R.string.try_again)
        val imageTopMargin = attributes.getDimension(R.styleable.EmptyPageLayout_empty_layout_image_top_margin, dip(123).toFloat())
        attributes.recycle()

        empty_image.setImageDrawable(emptyImage)
        empty_text.text = emptyText
        reload_btn.text = emptyBtnText

        empty_image.updateLayoutParams<MarginLayoutParams> {
            topMargin = imageTopMargin.toInt()
        }
    }

    fun setBtnVisible(visible: Boolean): EmptyPageLayout {
        reload_btn.isVisible = visible
        return this
    }

    fun setEmptyImage(@DrawableRes resId: Int): EmptyPageLayout {
        empty_image.setImageDrawable(ContextCompat.getDrawable(context,resId))
        return this
    }

    fun setEmptyText(str: String): EmptyPageLayout {
        empty_text.text = str
        return this
    }

    fun setTopMargin(margin: Int): EmptyPageLayout {
        empty_image.updateLayoutParams<MarginLayoutParams> {
            topMargin = margin
        }
        return this
    }

    fun setBtnClickListener(listener: OnClickListener): EmptyPageLayout {
        reload_btn.setOnClickListener(listener)
        return this
    }
}