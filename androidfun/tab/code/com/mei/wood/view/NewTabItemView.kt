package com.mei.wood.view

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.flyco.roundview.RoundTextView
import com.mei.orc.ext.dip
import com.mei.orc.util.string.maxNinetyNine
import com.mei.wood.R
import com.mei.wood.util.logDebug
import com.opensource.svgaplayer.SVGAImageView
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
import java.net.URL


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/28
 *
 *  melkor_item_bar.xml
 */


class NewTabItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attrs, defStyleAttr) {

    val bottom_bar_item_title: TextView by lazy {
        TextView(context).apply {
            id = R.id.bottom_bar_item_title
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                rules[ALIGN_PARENT_BOTTOM] = TRUE
                rules[CENTER_HORIZONTAL] = TRUE
                bottomMargin = dip(4.0f)
//                topMargin = dip(4.0f)
            }
            gravity = Gravity.CENTER
            includeFontPadding = false
            maxLines = 1
            setTextColor(ContextCompat.getColorStateList(context, R.color.tab_selected_color))
            textSize = 11.0f
        }
    }


    val bottom_bar_item_icon: ImageView by lazy {
        ImageView(context).apply {
            alpha = 1.0f
            id = R.id.bottom_bar_item_icon
            val wh = dip(24)
            layoutParams = LayoutParams(wh, wh).apply {
                rules[ABOVE] = R.id.bottom_bar_item_title
                rules[CENTER_HORIZONTAL] = TRUE
            }
        }
    }

    val bottom_bar_item_svg_icon: SVGAImageView by lazy {
        SVGAImageView(context).apply {
            alpha = 0.0f
            id = R.id.bottom_bar_item_svg_icon
            loops = 1
            clearsAfterStop = false
            scaleType = ImageView.ScaleType.FIT_XY
            val wh = dip(24)
            layoutParams = LayoutParams(wh, wh).apply {
                rules[ABOVE] = R.id.bottom_bar_item_title
                rules[CENTER_HORIZONTAL] = TRUE
            }
        }
    }

    val bottom_bar_red_point: TextView by lazy {
        RoundTextView(context).apply {
            id = R.id.bottom_bar_red_point
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, dip(16)).apply {
                rules[RIGHT_OF] = R.id.bottom_bar_item_svg_icon
                rules[ALIGN_TOP] = R.id.bottom_bar_item_svg_icon
                setMargins(dip(-8), 0, 0, 0)
            }
            gravity = Gravity.CENTER
            delegate.cornerRadius = 10
            delegate.backgroundColor = ContextCompat.getColor(context, android.R.color.holo_red_light)
            val padlr = dip(5)
            setPadding(padlr, 0, padlr, 0)
            includeFontPadding = false
            minWidth = dip(16)
            setTextColor(Color.WHITE)
            textSize = 12.0f
            visibility = GONE
        }
    }


    var changeAnimAssets: String = ""
        set(value) {
            field = value
            SVGAParser.shareParser().decodeFromAssets(value, null)
        }

    @DrawableRes
    var imgRes: Int = 0
        set(value) {
            field = value
            bottom_bar_item_icon.setImageResource(value)
        }
    var imgDrawable: StateListDrawable = StateListDrawable()
        set(value) {
            field = value
            bottom_bar_item_icon.setImageDrawable(value)
        }


    var title: String? = null
        set(value) {
            field = value
            bottom_bar_item_title.apply {
                isVisible = value.orEmpty().isNotEmpty()
                text = value
            }
        }
    var resNor: String? = null
    var resPre: String? = null
    var animation: String? = null

    /**
     *  -1 展示红点  0 不展示  >0展示
     */
    var messageNumber: Int = 0
        set(value) {
            field = value
            bottom_bar_red_point.apply {
                if (field == -1) {
                    //只展示红点
                    isVisible = true
                    text = ""
                    updateLayoutParams {
                        width = dip(10)
                        height = dip(10)
                    }
                } else {
                    isVisible = field > 0
                    text = field.maxNinetyNine()
                    updateLayoutParams {
                        width = LayoutParams.WRAP_CONTENT
                        height = dip(16)
                    }
                }
            }
        }

    private var nowSelected = false
    override fun setSelected(selected: Boolean) {
        if (nowSelected == selected) {
            return
        }
        nowSelected = selected

        (bottom_bar_item_svg_icon.tag as? ValueAnimator)?.cancel()

        val anim = ValueAnimator()
        val maxWH = 45.0f
        val minWH = 24.0f

        if (selected) {
            anim.setFloatValues(bottom_bar_item_svg_icon.alpha, 1.0f)
        } else {
            anim.setFloatValues(bottom_bar_item_svg_icon.alpha, 0.0f)
        }

        anim.addUpdateListener {
            val f = it.animatedValue as Float
            val wh = dip((minWH + (maxWH - minWH) * f))
            bottom_bar_item_svg_icon.updateLayoutParams {
                width = wh
                height = wh
            }
            bottom_bar_item_svg_icon.alpha = f
            bottom_bar_item_icon.alpha = 1.0f - f
        }

        anim.duration = 150
        bottom_bar_item_svg_icon.tag = anim
        bottom_bar_item_title.isSelected = selected

        if (selected) {
            if (changeAnimAssets.isNotEmpty() || animation.orEmpty().isNotEmpty()) {
                var url: URL? = null
                try {
                    url = URL(animation.orEmpty())
                } catch (e: Exception) {
                    logDebug(e.toString())
                }
                if (url != null) {
                    SVGAParser.shareParser().decodeFromURL(url, object : SVGAParser.ParseCompletion {
                        override fun onComplete(videoItem: SVGAVideoEntity) {
                            bottom_bar_item_svg_icon.setVideoItem(videoItem)
                            bottom_bar_item_svg_icon.startAnimation()
                        }

                        override fun onError() {
                            bottom_bar_item_svg_icon.setImageDrawable(getImageDrawable())
                            bottom_bar_item_svg_icon.isSelected = true
                        }
                    })
                } else {
                    SVGAParser.shareParser().decodeFromAssets(changeAnimAssets, object : SVGAParser.ParseCompletion {
                        override fun onComplete(videoItem: SVGAVideoEntity) {
                            bottom_bar_item_svg_icon.setVideoItem(videoItem)
                            bottom_bar_item_svg_icon.startAnimation()
                        }

                        override fun onError() {
                            bottom_bar_item_svg_icon.setImageDrawable(getImageDrawable())
                            bottom_bar_item_svg_icon.isSelected = true
                        }
                    })
                }

            } else {
                bottom_bar_item_svg_icon.setImageDrawable(getImageDrawable())
                bottom_bar_item_svg_icon.isSelected = true
            }
        }
        anim.start()
    }

    private fun getImageDrawable(): Drawable? {
        return if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    imgDrawable.stateCount > 1
                } else {
                    imgDrawable.state.size > 1
                }) imgDrawable else context.getDrawable(imgRes)
    }


    init {
        setLayerType(View.LAYER_TYPE_HARDWARE, null)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        addView(bottom_bar_item_title)
        addView(bottom_bar_item_icon)
        addView(bottom_bar_item_svg_icon)
        addView(bottom_bar_red_point)
    }

    fun loadNetIcon() {
        if (resNor.orEmpty().isNotEmpty() && resPre.orEmpty().isNotEmpty()) {
            loadNetUrl(resNor.orEmpty(), resPre.orEmpty(), imgDrawable, bottom_bar_item_icon)
        }
    }


}

fun NewTabItemView.loadNetUrl(resNorUrl: String, resPreUrl: String, imgDrawable: StateListDrawable, imageView: ImageView) {
    if (resNorUrl.isEmpty())
        return
    Glide.with(context)
            .asDrawable()
            .load(resNorUrl)
            .into(object : CustomTarget<Drawable>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    imgDrawable.addState(IntArray(1) { -android.R.attr.state_pressed }, resource)
                    Glide.with(context)
                            .asDrawable()
                            .load(resPreUrl)
                            .into(object : CustomTarget<Drawable>() {
                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                    imgDrawable.addState(IntArray(1) { android.R.attr.state_pressed }, resource)
                                    imageView.setImageDrawable(imgDrawable)
                                }

                            })
                }
            })
}