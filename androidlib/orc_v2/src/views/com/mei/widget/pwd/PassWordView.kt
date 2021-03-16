package com.mei.widget.pwd

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import com.mei.orc.R
import kotlin.properties.Delegates

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/24
 */

interface OnPasswordChangedListener {
    /**
     * 方案改变了
     * @param isOver 是否已输入到最长
     */
    fun onChanged(psw: String, isOver: Boolean)
}

@Suppress("ConvertSecondaryConstructorToPrimary")
@SuppressLint("CustomViewStyleable")
class PassWordView : FrameLayout {

    /**密码长度**/
    var passWordLength: Int by Delegates.observable(4) { _, _, _ ->
        initView()
    }

    /**显示的文本颜色**/
    @ColorInt
    private var textColor: Int = Color.BLACK

    /**占位符**/
    private var placeholder: String = "●"

    @ColorInt
    private var lineColor: Int = Color.parseColor("#888888")
    private var lineWidth: Int = dip(1)

    var onPasswordChangedListener: OnPasswordChangedListener? = null

    @JvmOverloads
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : super(context, attrs, defStyleAttr) {
        val typeArr = context.obtainStyledAttributes(attrs, R.styleable.password_style)
        textColor = typeArr.getColor(R.styleable.password_style_pw_text_color, Color.BLACK)
        lineColor = typeArr.getColor(R.styleable.password_style_pw_line_color, Color.parseColor("#888888"))
        lineWidth = typeArr.getDimension(R.styleable.password_style_pw_line_width, dip(1).toFloat()).toInt()
        val holder = typeArr.getString(R.styleable.password_style_pw_text_holder).orEmpty()
        placeholder = if (holder.isNotEmpty()) holder else "●"
        passWordLength = typeArr.getInt(R.styleable.password_style_pw_length, 4)

        typeArr.recycle()

    }

    private fun initView() {
        removeAllViews()
        cacheTextArr.clear()
        cacheTextArr.addAll(viewArr)

        cacheRealEdit = realEdit
        cacheContainer = container
        addView(cacheRealEdit)
        addView(cacheContainer)
        forceInputViewGetFocus()
        cacheContainer.bringToFront()
    }

    private val textWatcher: TextWatcher by lazy {
        object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val txt = s?.toString() ?: ""
                onPasswordChangedListener?.onChanged(txt, txt.length == passWordLength)
                cacheTextArr.forEachIndexed { index, textView ->
                    textView.text = if (index < txt.length) placeholder else ""
                }
            }

        }
    }

    private var cacheRealEdit: EditText by Delegates.notNull()

    /**背后真实输入的EditText**/
    private var realEdit: EditText
        set(_) {}
        get() = EditText(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            background = null
            filters = arrayOf<InputFilter>(InputFilter.LengthFilter(passWordLength))
            isCursorVisible = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                focusable = FOCUSABLE
            }
            alpha = 0f
            isFocusable = true
            isFocusableInTouchMode = true
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            addTextChangedListener(textWatcher)
        }
    private var cacheTextArr = arrayListOf<TextView>()

    /**用于展示的TextView**/
    private var viewArr: List<TextView>
        set(_) {}
        get() = arrayListOf<TextView>().apply {
            for (i in 1..passWordLength) {
                add(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1f)
                    setTextColor(textColor)
                    gravity = Gravity.CENTER
                    maxLines = 1
                })
            }
        }

    private var cacheContainer: LinearLayout by Delegates.notNull()
    private var container: LinearLayout
        set(_) {}
        get() = LinearLayout(context).apply {
            layoutParams = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT)
            cacheTextArr.forEachIndexed { index, textView ->
                if (lineWidth > 0 && index > 0) {
                    /**add divider**/
                    addView(View(context).apply {
                        layoutParams = LinearLayout.LayoutParams(lineWidth, LinearLayout.LayoutParams.MATCH_PARENT)
                        setBackgroundColor(lineColor)
                    })
                }
                addView(textView)
            }
            /** add outer Line**/
            if (lineWidth > 0) {
                background = GradientDrawable().apply {
                    setStroke(lineWidth, lineColor)
                }
            }
            setOnClickListener { forceInputViewGetFocus() }

        }


    fun getPassWord(): String {
        return cacheRealEdit.text.toString()
    }

    fun clearPassword() {
        cacheRealEdit.setText("")
    }

    @JvmOverloads
    fun setPassword(password: String, forceChangeLength: Boolean = false) {
        if (forceChangeLength) passWordLength = password.length
        cacheRealEdit.setText(password)
        cacheRealEdit.setSelection(password.length)

    }

    private fun dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    fun forceInputViewGetFocus() {
        cacheRealEdit.isFocusable = true
        cacheRealEdit.isFocusableInTouchMode = true
        cacheRealEdit.requestFocus()
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        imm?.showSoftInput(cacheRealEdit, InputMethodManager.SHOW_IMPLICIT)
    }
}






