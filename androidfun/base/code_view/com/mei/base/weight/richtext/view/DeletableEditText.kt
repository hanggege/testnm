package com.mei.base.weight.richtext.view

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import com.mei.wood.R

import com.rockerhieu.emojicon.EmojiconEditText

/**
 * 这个是从stackOverFlow上面找到的解决方案，主要用途是处理软键盘回删按钮backSpace时回调OnKeyListener
 *
 * @author xmuSistone
 * 重要：@param defStyleAttr使用0时会造成点击获取焦点失败
 */
class DeletableEditText @JvmOverloads constructor(context: Context, attributeSet: AttributeSet? = null, defStyleAttr: Int = R.attr.editTextStyle)
    : EmojiconEditText(context, attributeSet, defStyleAttr) {

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return DeleteInputConnection(super.onCreateInputConnection(outAttrs), true)
    }

    private inner class DeleteInputConnection internal constructor(target: InputConnection, mutable: Boolean)
        : InputConnectionWrapper(target, mutable) {

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            return if (beforeLength == 1 && afterLength == 0) {
                sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            } else {
                super.deleteSurroundingText(beforeLength, afterLength)
            }
        }
    }
}