package com.mei.base.weight.richtext

import android.animation.LayoutTransition
import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View.*
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mei.base.weight.richtext.delegate.RichTextDelegate
import com.mei.base.weight.richtext.info.FindType
import com.mei.base.weight.richtext.info.RichInfo
import com.mei.base.weight.richtext.info.RichTag
import com.mei.base.weight.richtext.info.RichType
import com.mei.base.weight.richtext.listener.LastEditorWatcher
import com.mei.base.weight.richtext.listener.RichTextImageDeleteListener
import com.mei.base.weight.richtext.listener.RichTextWatcher
import com.mei.base.weight.richtext.view.DataImageLinearLayout
import com.mei.orc.util.string.stringConcate
import com.mei.wood.R
import com.rockerhieu.emojicon.EmojiconEditText


/**
 *
 * @author Created by Ling on 2019-07-05
 * 这是一个富文本编辑器，给外部提供insertImage接口，添加的图片跟当前光标所在位置有关 要用scrollview包裹，推荐[WatchScrollView]
 * 有更强的自定义性，可在xml文件中配置
 */

class RichTextEditor @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = 0)
    : RelativeLayout(context, attributeSet, defStyleAttr) {
    var delegate: RichTextDelegate = RichTextDelegate(this, context, attributeSet, defStyleAttr)
    internal var inflater: LayoutInflater = LayoutInflater.from(context)
    internal val DELAY_DISPLAY_TIME = 500L

    private var firstEdit: EmojiconEditText? = null // 最近被聚焦的EditText
    internal var lastFocusEdit: EmojiconEditText? = null // 最近被聚焦的EditText
    internal var viewTagIndex = 1 // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    internal var lastSelectView: RelativeLayout? = null//上一次操作的图片，用于图片显示删除按钮与否

    // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    internal val allLayout: LinearLayout by lazy {
        val temp = LinearLayout(context)
        temp.orientation = LinearLayout.VERTICAL
        temp.setBackgroundColor(Color.WHITE)
        temp
    }

    // 只在图片View添加或remove时，触发transition动画
    internal val mTransition: LayoutTransition by lazy {
        val temp = LayoutTransition()
//        temp.addTransitionListener(object : LayoutTransition.TransitionListener {
//
//            override fun startTransition(transition: LayoutTransition, container: ViewGroup, view: View,
//                                         transitionType: Int) {
//
//            }
//
//            override fun endTransition(transition: LayoutTransition, container: ViewGroup, view: View,
//                                       transitionType: Int) {
//                if (!transition.isRunning && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
//                    // transition动画结束，合并EditText
//                    // mergeEditText();
//                }
//            }
//        })
        temp.setDuration(300)
        temp
    }

    internal val keyListener: OnKeyListener by lazy {
        OnKeyListener { v, keyCode, event ->
            if (event?.action == KeyEvent.ACTION_DOWN
                    && keyCode == KeyEvent.KEYCODE_DEL) {
                val edit = v as? EmojiconEditText
                onBackspacePress(edit)
            }
            false
        }
    }

    // 3. 图片叉掉处理
    internal val btnListener by lazy {
        OnClickListener {
            val parentView = it.parent as? RelativeLayout
            onImageCloseClick(parentView)
        }
    }

    // 图片点击监听
    internal val imageListener: OnClickListener by lazy {
        OnClickListener {
            onSelectedTagChanged(it)
        }
    }

    // 所有EditText的焦点监听listener
    internal val focusListener: OnFocusChangeListener by lazy {
        OnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setLastEdit(v as? EmojiconEditText)
                onSelectedTagChanged(v)
            }
        }
    }

    /** 对外设置的监听 **/
    //图片删除
    var imageDeleteListener: RichTextImageDeleteListener? = null
    var richTextWatcher: RichTextWatcher? = null
        set(value) {
            field = value
            lastFocusEdit?.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                    value?.beforeTextChanged(s, start, count, after)
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

                }

                override fun afterTextChanged(s: Editable) {
                    value?.onTextChanged(getTextAll(FindType.TXT))
                    value?.afterTextChanged(s)
                }
            })
        }

    //监听获取最后一个文本编辑器
    var lastEditorWatcher: LastEditorWatcher? = null

    /** 对外设置的监听 over**/

    init {
        val layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        addView(allLayout, layoutParams)
        val firstEditParam = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        firstEditParam.setMargins(delegate.mMarginLeft, delegate.mTextMarginTop, delegate.mMarginRight, delegate.mTextMarginBottom)
        firstEdit = createEditText(delegate.hint)
        allLayout.addView(firstEdit, firstEditParam)
        setLastEdit(firstEdit)
    }

    /**
     * 对外提供的接口, 生成编辑数据上传
     */
    fun buildEditList(type: FindType): ArrayList<RichInfo> {
        val dataList = arrayListOf<RichInfo>()
        val num = allLayout.childCount
        for (index in 0 until num) {
            when (val itemView = allLayout.getChildAt(index)) {
                is EditText -> {
                    val inputStr = itemView.text?.toString().orEmpty()
                    if (inputStr.isNotEmpty() && type !== FindType.IMG) {
                        dataList.add(RichInfo(formatRichEdit(inputStr), RichType.TXT))
                    }
                }
                is RelativeLayout -> {
                    val item = itemView.findViewById<DataImageLinearLayout>(R.id.img_container)
                    val imagePath = item.absolutePath.orEmpty()
                    if (imagePath.isNotEmpty() && type === FindType.IMG) {
                        dataList.add(RichInfo(imagePath, RichType.IMG))
                    } else if (type === FindType.ALL) {
                        dataList.add(RichInfo(stringConcate(RichTag.ImgStartTag, imagePath, RichTag.ImgEndTag),
                                RichType.TXT))
                    }
                }
            }
        }
        return dataList
    }

    /**
     * 获取带标签的图片纯文本
     */
    fun getTextAll(): String {
        return getTextAll(FindType.ALL)
    }

    fun setText(richTxt: String) {
        this.postDelayed({ fillText(richTxt) }, DELAY_DISPLAY_TIME)
    }

    /**
     * 根据绝对路径添加view
     */
    fun insertImage(imagePath: String) {
        val edit = lastFocusEdit?.text?.toString().orEmpty()
        if (edit.isEmpty() && allLayout.childCount > 1) {
            val input = ""
            lastFocusEdit?.setText(input)
            lastFocusEdit?.setSelection(input.length)
        }
        val widthAndHeight = getScaledBitmapWidthAndHeight(imagePath, width)
        if (widthAndHeight.size != 2) return
        insertImage(widthAndHeight[0], widthAndHeight[1], imagePath)
    }

    fun getLastEdit(): EmojiconEditText? {
        return lastFocusEdit
    }

    /**
     * 退出时清除所有数据
     */
    fun clearAllView() {
        val input = getTextAll()
        if (input.isNotEmpty()) {
            allLayout.postDelayed({
                val num = allLayout.childCount
                for (i in 0 until num) {
                    val itemView = allLayout.getChildAt(i)
                    if (itemView !== firstEdit) {
                        allLayout.removeView(itemView)
                    } else {
                        firstEdit?.setText("")
                        setLastEdit(firstEdit)
                    }
                }
            }, DELAY_DISPLAY_TIME)
        }
    }

    /**
     * 获取全部的图片列表
     */
    fun getImgList(): ArrayList<String> {
        val imgList = arrayListOf<String>()
        val infoList = buildEditList(FindType.IMG)
        for (info in infoList) {
            if (info.text.isNotEmpty()) {
                imgList.add(info.text)
            }
        }
        return imgList
    }

    fun setHint(hint: String) {
        if (firstEdit != null) {
            delegate.hint = hint
            firstEdit?.hint = hint
        }
    }
}