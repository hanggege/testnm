package com.mei.live.views

import android.text.SpannableStringBuilder

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.views
 * @ClassName:      SpannableStringBuilderAllVer
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/5/27 16:13
 * @UpdateUser:
 * @UpdateDate:     2020/5/27 16:13
 * @UpdateRemark:
 * @Version:
 */
class SpannableStringBuilderAllVer : SpannableStringBuilder() {


    override fun append(text: CharSequence?): SpannableStringBuilderAllVer {
        if (text == null) return this
        val length = length
        return replace(length, length, text, 0, text.length) as SpannableStringBuilderAllVer
    }

    /** 该方法在原API里面只支持API21或者以上，这里抽取出来以适应低版本  */
    override fun append(text: CharSequence?, what: Any?, flags: Int): SpannableStringBuilderAllVer {
        if (text == null) return this
        val start = length
        append(text)
        setSpan(what, start, length, flags)
        return this
    }
}
