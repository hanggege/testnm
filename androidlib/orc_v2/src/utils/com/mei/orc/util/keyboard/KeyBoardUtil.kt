package com.mei.orc.util.keyboard

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import com.mei.orc.util.handler.GlobalUIHandler


/**
 *
 * @author Created by Ling on 2019-07-19
 * 键盘的打开与关闭
 */

/**
 * 隐藏软键盘，分为实时隐藏和延时隐藏
 */
fun Activity.hideKeyBoard() {
    try {
        val focusView = currentFocus
        focusView?.let {
            (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.hideKeyBoard() {
    try {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun View.hideKeyBoardForce() {
    try {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(windowToken, 0)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun Activity.hideKeyBoardDelay() {
    GlobalUIHandler.schedule({
        try {
            val focusView = currentFocus
            focusView?.let {
                (this.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 300)
}

fun View.hideKeyBoardDelay() {
    GlobalUIHandler.schedule({
        try {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 300)
}

/**
 * 显示软键盘，分为实时显示和延时显示
 */
fun Activity.showKeyBoard() {
    try {
        val focusView = currentFocus
        focusView?.let {
            (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.showKeyBoard() {
    try {
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.showKeyBoardDelay() {
    GlobalUIHandler.schedule({
        try {
            (context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 300)
}


fun Activity.showKeyBoardDelay() {
    GlobalUIHandler.schedule({
        try {
            val focusView = currentFocus
            focusView?.let {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 300)
}

/**
 * 软键盘切换，分为实时切换和延时切换
 */
fun EditText.toggleSoftwareKeyboard() {
    try {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.let {
            if (it.isActive) {
                it.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            } else {
                it.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun EditText.toggleSoftwareKeyboardDelay() {
    GlobalUIHandler.schedule({
        try {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.let {
                if (it.isActive) {
                    it.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                } else {
                    it.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }, 300)
}

fun showSoftInputFromWindow(editText: EditText) {
    editText.isFocusable = true
    editText.isFocusableInTouchMode = true
    editText.requestFocus()
    val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputManager.showSoftInput(editText, 0)
}