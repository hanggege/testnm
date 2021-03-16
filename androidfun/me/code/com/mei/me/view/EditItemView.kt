package com.mei.me.view

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.orc.ext.dip
import com.mei.orc.ext.selectBy
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.wood.R
import kotlinx.android.synthetic.main.item_edit_layout.view.*


/**
 *
 * @author Created by Ling on 2019/1/16
 */
class EditItemView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
        ConstraintLayout(context, attrs, defStyleAttr) {
    private var initEditText: CharSequence? = null

    var arrowVisible: Boolean = true
        set(value) {
            field = value
            edit_arrow.isVisible = value
        }
    var textVisible: Boolean = true
        set(value) {
            field = value
            edit_text.isVisible = value
        }
    var editText: CharSequence? = ""
        get() = edit_text.text.trim().toString()
        set(value) {
            if (initEditText == null) {
                initEditText = value
            }
            field = value
            edit_text.setText(value)
            edit_text.setSelection(edit_text.text.length, edit_text.text.length)
            text_view_b.text = value
            edit_text_label.text = value
            tips_hint_tv.text = value
        }
    var editHint: String? = ""
        set(value) {
            field = value
            edit_text.hint = value
        }

    var editTitle: CharSequence? = ""
        set(value) {
            field = value
            edit_title.text = value
        }

    var editTitleBold: Boolean = false
        set(value) {
            if (field != value) {
                edit_title.paint.isFakeBoldText = value
                field = value
            }
        }

    var editIcon: Drawable? = null
        set(value) {
            field = value
            edit_icon.setImageDrawable(value)
        }

    var editTitleColor: Int = Color.BLACK
        set(value) {
            field = value
            edit_title.setTextColor(value)
        }
    var editTextColor: Int = Color.BLACK
        set(value) {
            field = value
            this.edit_text.setTextColor(value)
        }
    var editTextSize: Float = 13f

    //        set(value) {
//            field = value
//            edit_text.setTextSize(TypedValue.COMPLEX_UNIT_SP,value)
//        }
    var isFocus = true
    var editTextType = 0
        set(value) {
            if (field != value) {
                field = value
                updateType()
            }
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.item_edit_layout, this)
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.EditItemView)
        arrowVisible = typedArray.getBoolean(R.styleable.EditItemView_edit_arrow_visible, true)
        textVisible = typedArray.getBoolean(R.styleable.EditItemView_edit_text_visible, true)
        editTitle = typedArray.getText(R.styleable.EditItemView_edit_title)?.toString() ?: ""
        editTitleBold = typedArray.getBoolean(R.styleable.EditItemView_edit_title_bold, false)
        editHint = typedArray.getText(R.styleable.EditItemView_edit_hint)?.toString() ?: ""
        editText = typedArray.getText(R.styleable.EditItemView_edit_text)?.toString() ?: ""
        editIcon = typedArray.getDrawable(R.styleable.EditItemView_edit_icon)
        editTitleColor = typedArray.getColor(R.styleable.EditItemView_edit_title_color, Color.BLACK)
        editTextColor = typedArray.getColor(R.styleable.EditItemView_edit_text_color, Color.BLACK)
        editTextSize = typedArray.getDimension(R.styleable.EditItemView_edit_title_size, 13f)
        isFocus = typedArray.getBoolean(R.styleable.EditItemView_edit_text_focus, true)
        editTextType = typedArray.getInt(R.styleable.EditItemView_edit_textview_type, 0)
        typedArray.recycle()
        initView()
    }

    private fun initView() {
        edit_arrow.isVisible = arrowVisible
        edit_text.isVisible = textVisible
        edit_text.setText(editText)
        edit_text.hint = editHint
        edit_title.text = editTitle
        edit_title.setTextColor(editTitleColor)
        edit_text.setTextColor(editTextColor)
        text_view_b.setTextColor(editTextColor)
        edit_text.isEnabled = isFocus

        updateType()

        edit_title.updateLayoutParams<LayoutParams> {
            marginStart = (editIcon == null).selectBy(dip(15), dip(50))
        }

    }

    private fun updateType() {
        when (editTextType) {
            0 -> { // EditText
                text_view_b.visibility = View.INVISIBLE
                edit_text.visibility = View.VISIBLE
            }
            1 -> { // TextView
                text_view_b.text = editText
                text_view_b.visibility = View.VISIBLE
                edit_text.visibility = View.INVISIBLE
            }
            2 -> { // LabelText
                edit_text.isGone = true
                text_view_b.isGone = true
                edit_text_label.text = editText
            }
        }
    }

    fun refreshInitText() {
        initEditText = editText
    }

    fun displayEditIcon(url: String, defaultIcon: Int) {
        edit_icon.glideDisplay(url, defaultIcon)
    }

    fun displayTipsIcon(url: String?) {
        tips_icon_iv.glideDisplay(url)
        tips_icon_fl.isVisible = url?.isNotEmpty() == true
    }

    fun requestEditFocus() {
        edit_text.requestFocus(edit_text.length())
        edit_text.setSelection(edit_text.text.length, edit_text.text.length)
        edit_text.showKeyBoardDelay()
    }

    fun isChanged(): Boolean = !TextUtils.equals(initEditText, editText)

}

class FocusEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return isEnabled && super.onTouchEvent(event)
    }


}