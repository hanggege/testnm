package com.mei.base.weight.richtext

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.mei.base.weight.richtext.info.FindType
import com.mei.base.weight.richtext.info.RichTag
import com.mei.orc.util.string.stringConcate
import com.mei.wood.R
import com.rockerhieu.emojicon.EmojiconEditText

/**
 *
 * @author Created by Ling on 2019-07-08
 * 富文本的文本处理
 */

/**
 * 处理软键盘backSpace回退事件
 *
 * @param editTxt 光标所在的文本输入框
 */
fun RichTextEditor.onBackspacePress(editTxt: EmojiconEditText?) {
    val startSelection = editTxt?.selectionStart
    // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
    if (startSelection == 0) {
        val editIndex = allLayout.indexOfChild(editTxt)
        val preView: View? = allLayout.getChildAt(editIndex - 1) // 如果editIndex-1<0, 则返回的是null
        preView?.let {
            when (it) {
                is RelativeLayout -> onImageCloseClick(it)// 光标EditText的上一个view对应的是图片
                is EmojiconEditText -> {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    val str1 = editTxt.text.toString()
                    val str2 = it.text.toString()
                    // 合并文本view时，不需要transition动画
                    allLayout.layoutTransition = null
                    allLayout.removeView(editTxt)
                    allLayout.layoutTransition = mTransition // 恢复transition动画
                    // 文本合并
                    it.setText(String.format("%s%s", str2, str1))
                    it.requestFocus()
                    it.setSelection(str2.length, str2.length)
                    setLastEdit(it)
                }
            }
        }
    }
}

/**
 * 获取输入的纯文本
 */
internal fun RichTextEditor.getTextOutImg(): String {
    return getTextAll(FindType.TXT)
}

fun RichTextEditor.getTextAll(type: FindType): String {
    var result = ""
    val list = buildEditList(type)
    for (info in list) {
        result = stringConcate(result, info.text)
    }
    return result
}

/**
 * 设置上个文本编辑器
 */
internal fun RichTextEditor.setLastEdit(currEdit: EmojiconEditText?, vararg editStr: String) {
    lastFocusEdit = currEdit
    if (richTextWatcher != null) {//重新赋值，调动addTextChangedListener方法，必不可少哦
        richTextWatcher = richTextWatcher
    }
    if (currEdit != null)
        lastEditorWatcher?.onLastEditor(currEdit)
    if (editStr.isNotEmpty() && editStr[0].isNotEmpty()) {
        currEdit?.setText(editStr[0])
    }
}

/**
 * 生成文本输入框
 */
internal fun RichTextEditor.createEditText(hint: CharSequence?): EmojiconEditText? {
    val editText = inflater.inflate(R.layout.rich_edit_item, null) as? EmojiconEditText
    editText?.setHintTextColor(delegate.mHintColor)
    editText?.setTextColor(delegate.mTextColor)
    editText?.setEmojiconSize(delegate.mTextSize + 4)
    editText?.setTextSize(TypedValue.COMPLEX_UNIT_PX, delegate.mTextSize.toFloat())
    editText?.setLineSpacing(0f, delegate.mLineSpacingMultiplier)
    editText?.setOnKeyListener(keyListener)
    editText?.tag = viewTagIndex++
    editText?.hint = hint
    editText?.onFocusChangeListener = focusListener
    return editText
}

/**
 * 根据文本填充，根据tag取本地图片地址
 */
internal fun RichTextEditor.fillText(richTxt: String?) {
    if (richTxt.orEmpty().isNotEmpty()) {
        var startIndex = 0
        while (startIndex < richTxt!!.length) {
            val editTxt: String
            val startTag = richTxt.indexOf(RichTag.ImgStartTag, startIndex)
            val endTag = richTxt.indexOf(RichTag.ImgEndTag, startIndex)
            if (endTag in 1 until startTag && startTag > 0) {
                editTxt = richTxt.substring(startIndex, endTag)
                startIndex = endTag + RichTag.ImgEndTag.length
                insertImage(editTxt)
            } else if (startTag >= 0) {
                editTxt = richTxt.substring(startIndex, startTag)
                startIndex = startTag + RichTag.ImgStartTag.length
                lastFocusEdit?.setText(editTxt)
                lastFocusEdit?.setSelection(editTxt.length, editTxt.length)
            } else if (endTag > 0) {
                editTxt = richTxt.substring(startIndex, endTag)
                startIndex = endTag + RichTag.ImgEndTag.length
                insertImage(editTxt)
            } else {
                editTxt = richTxt.substring(startIndex, richTxt.length)
                startIndex = richTxt.length
                lastFocusEdit?.setText(editTxt)
                lastFocusEdit?.setSelection(editTxt.length, editTxt.length)
            }
        }
    }
}


/**
 * 在特定位置插入EditText
 *
 * @param index   位置
 * @param editStr EditText显示的文字
 */
internal fun RichTextEditor.addEditTextAtIndex(index: Int, editStr: String) {
    val editParam = LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT)
    editParam.setMargins(delegate.mMarginLeft, delegate.mTextMarginTop, delegate.mMarginRight, delegate.mTextMarginBottom)
    val editText2 = createEditText("继续输入")
    editText2?.layoutParams = editParam

    // 请注意此处，EditText添加、或删除不触动Transition动画
    allLayout.layoutTransition = null
    allLayout.addView(editText2, index)
    allLayout.layoutTransition = mTransition // remove之后恢复transition动画
    setLastEdit(editText2, editStr)
    lastFocusEdit?.hint = ""
}

/**
 * 隐藏小键盘
 */
fun RichTextEditor.hideKeyBoard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    imm?.hideSoftInputFromWindow(lastFocusEdit?.windowToken, 0)
}

fun RichTextEditor.showKeyBoard() {
    val mgr = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
    mgr?.showSoftInput(lastFocusEdit, InputMethodManager.SHOW_IMPLICIT)
}