package com.mei.widget.tagtextview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import com.mei.orc.R
import java.util.*


/**
 * Created by xiaozhiguang on 2017/12/14.
 */


@Suppress("unused")
class TagTextView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        AppCompatTextView(context, attrs, defStyleAttr) {

    enum class TAG_INDEX {
        START,
        END
    }

    private val padRect = Rect(4, 0, 4, 0)
    private val padContentRect = Rect(4, 1, 4, 1)

    private var tagsBackgroundStyle: Int = R.drawable.shape_textview_tags_bg

    //sp
    private var tagTextSize = 10       //  标签的字体大小
    private var tagTextColor = "#FF08B1FF"    //   标签的字体颜色

    private var content_buffer: StringBuilder = StringBuilder("")

    private var tagsIndex = TAG_INDEX.START  //  默认标签在开始的位置

    /**
     * 设置标签的背景样式
     *
     * @param tagTextSize 你需要替换的tag文字字体大小
     */
    fun setTagTextSize(tagTextSize: Int) {
        this.tagTextSize = tagTextSize
    }

    /**
     * 设置标签的背景样式
     *
     * @param tagTextColor 你需要替换的tag的文字颜色
     */
    fun setTagTextColor(tagTextColor: String) {
        this.tagTextColor = tagTextColor
    }

    /**
     * 设置标签的背景样式
     *
     * @param tagsBackgroundStyle 你需要替换的tag背景样式
     */
    fun setTagsBackgroundStyle(tagsBackgroundStyle: Int) {
        this.tagsBackgroundStyle = tagsBackgroundStyle
    }


    /**
     * 设置间距
     */
    fun setTagPadding(leftDp: Int, topDp: Int, rightDp: Int, bottomDp: Int) {
        padRect.set(leftDp, topDp, rightDp, bottomDp)
    }

    /**
     * 设置内容间距
     */
    fun setTagContentPadding(leftDp: Int, topDp: Int, rightDp: Int, bottomDp: Int) {
        padContentRect.set(leftDp, topDp, rightDp, bottomDp)
    }

    /**
     * 设置标签是在头部还是尾部
     *
     * @param tagsIndex 头部还是尾部显示tag
     */
    fun setTagsIndex(tagsIndex: TAG_INDEX) {
        this.tagsIndex = tagsIndex
    }

    /**
     * 设置标签和文字内容(单个)
     *
     * @param tag     标签内容
     * @param content 标签文字
     */
    fun setSingleTagAndContent(tag: String, content: String) {
        val tagList = ArrayList<String>()
        if (tag.isNotEmpty()) {
            tagList.add(tag)
        }
        setMultiTagAndContent(tagList, content)
    }

    /**
     * 设置标签和文字内容(多个)
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    fun setMultiTagAndContent(tags: List<String>, content: String) {
        if (tagsIndex == TAG_INDEX.START) {
            setTagStart(tags, content)
        } else {
            setTagEnd(tags, content)
        }
    }

    /**
     * 标签显示在头部位置
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    fun setTagStart(tags: List<String>, content: String) {
        var endIndex = 0
        var startIndex = 1
        content_buffer.clear()

        for (item in tags) {
            content_buffer.append(item)
        }
        content_buffer.append(content)
        val spannableString = SpannableString(content_buffer)
        for (i in tags.indices) {
            val item = tags[i]
            endIndex += item.length
            val span = CenterImageSpan(getTagDrawable(item))
            spannableString.setSpan(span, startIndex - 1, endIndex, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            startIndex += item.length
        }
        text = spannableString
        gravity = Gravity.CENTER_VERTICAL
    }


    /**
     * 设置图片
     *
     * @param resID   资源ID
     * @param content 文字内容
     */
    fun setTagImageStart(context: Context, resID: Int, content: String, width: Int, height: Int) {
        content_buffer.clear()
        content_buffer.append("**$content")//  两个字符占位
        val spannableString = SpannableString(content_buffer)
        val bitmap = BitmapFactory.decodeResource(resources, resID)
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(0, 0, dp2px(context, width.toFloat()), dp2px(context, height.toFloat()))
        val span = CenterImageSpan(drawable)
        spannableString.setSpan(span, 0, 2, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        text = spannableString
        gravity = Gravity.CENTER_VERTICAL
    }

    /**
     * 标签显示在头部位置
     *
     * @param tags    标签内容
     * @param content 标签文字
     */
    @Suppress("UNUSED_PARAMETER")
    fun setTagEnd(tags: List<String>, content: String) {
        content_buffer.clear()
        for (item in tags) {
            content_buffer.append(item)
        }
        val spannableString = SpannableString(content_buffer)
        for (i in tags.indices) {
            val item = tags[i]
            val span = CenterImageSpan(getTagDrawable(item))
            spannableString.setSpan(span, content_buffer.length - item.length, content_buffer.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        }
        text = spannableString
        gravity = Gravity.CENTER_VERTICAL
    }

    /**
     * 指定位置设置标签
     *
     * @param start   开始位置从0开始
     * @param end     结束位置长度-1
     * @param content 文字内容
     */
    fun setTagAnyway(start: Int, end: Int, content: String) {
        val item = content.substring(start, end)
        val span = CenterImageSpan(getTagDrawable(item))
        val spannableString = SpannableString(content)
        spannableString.setSpan(span, start, end, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        text = spannableString
        gravity = Gravity.CENTER_VERTICAL
    }


    private fun getTagDrawable(content: String): Drawable {
        val tagViewRoot = FrameLayout(context).apply {
            //设置tag左右间距
            setPadding(padRect.left, padRect.top, padRect.right, padRect.bottom)
        }

        val tagView = TextView(context).apply {
            setTextColor(Color.parseColor(tagTextColor))
            textSize = tagTextSize.toFloat()
            text = content
            //设置tag内部左右间距
            setPadding(padContentRect.left, padContentRect.top, padContentRect.right, padContentRect.bottom)
            setBackgroundResource(tagsBackgroundStyle)
        }
        tagViewRoot.addView(tagView)

        val bitmap = createBitmapFromView(tagViewRoot)
        val drawable = BitmapDrawable(resources, bitmap)
        drawable.setBounds(0, 0, tagViewRoot.measuredWidth, tagViewRoot.measuredHeight)
        return drawable
    }


    /**
     * Android中dp和pix互相转化
     *
     * @param dpValue dp值
     * @return
     */
    private fun dp2px(context: Context, dpValue: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    private fun createBitmapFromView(view: View): Bitmap {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        val bitmap = Bitmap.createBitmap(view.measuredWidth,
                view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val background = view.background
        background?.draw(canvas)
        view.draw(canvas)
        return bitmap
    }


}