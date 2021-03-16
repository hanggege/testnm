package com.mei.orc.ext

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/2/7.
 */
fun Context.color(res: Int): Int = ContextCompat.getColor(this, res)

fun Context.drawable(res: Int): Drawable? = ContextCompat.getDrawable(this, res)

fun Context.layoutInflaterKt(res: Int): View = LayoutInflater.from(this).inflate(res, null, false)

fun Context.layoutInflaterKt(res: Int, parent: ViewGroup): View = LayoutInflater.from(this).inflate(res, parent, false)

fun View.layoutInflaterKt(res: Int): View = LayoutInflater.from(context).inflate(res, null, false)

fun ViewGroup.layoutInflaterKtToParent(res: Int): View = LayoutInflater.from(context).inflate(res, this, false)

fun ViewGroup.layoutInflaterKtToParentAttach(res: Int): View = LayoutInflater.from(context).inflate(res, this)

