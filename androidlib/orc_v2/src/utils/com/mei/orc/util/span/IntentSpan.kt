package com.mei.orc.util.span

import android.os.Parcel
import android.text.ParcelableSpan
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View
import androidx.annotation.ColorInt

/**
 * Created by Joker on 2015/5/27.
 * textview 点击一部分字段跳转
 */
class IntentSpan(val clickBack: (View) -> Unit = {}, @ColorInt val color: Int) : ClickableSpan(), ParcelableSpan {

    //
    //  Needed to work on Android 6.0
    val spanTypeIdInternal: Int
        get() = spanTypeId

    override fun updateDrawState(ds: TextPaint) {
        ds.color = color
        ds.isUnderlineText = false
    }

    override fun onClick(sourceView: View) {
        clickBack(sourceView)
    }


    override fun getSpanTypeId(): Int {
        return 100
    }


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flag: Int) {
        // don't write to parcel
    }

    fun writeToParcelInternal(dest: Parcel, flags: Int) {
        writeToParcel(dest, flags)
    }

}