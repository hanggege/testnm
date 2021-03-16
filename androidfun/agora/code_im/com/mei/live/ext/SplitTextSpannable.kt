package com.mei.live.ext

import android.graphics.Color
import android.text.SpannableStringBuilder
import androidx.annotation.ColorInt
import com.joker.im.custom.chick.SplitText
import com.mei.init.meiApplication
import com.mei.init.spiceHolder
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.span.CustomImageSpan
import com.mei.orc.util.string.decodeUrl
import com.mei.wood.BuildConfig
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.appendSpannable
import com.mei.wood.extensions.executeKt
import com.net.network.report.MessageClickRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/8/4
 */
val richImageDic = hashMapOf<String, Int>().apply {
    put("[心币]", R.drawable.icon_heart_money)
    put("[钻石]", R.drawable.icon_service_list_masonry)
}

fun String?.parseColor(@ColorInt defaultColor: Int) =
        try {
            Color.parseColor(this.orEmpty())
        } catch (e: Exception) {
            defaultColor
        }

fun SpannableStringBuilder.appendSplitSpannable(list: List<SplitText>, @ColorInt defaultColor: Int,
                                                click: ((action: String) -> Unit)? = null): SpannableStringBuilder {
    list.forEach {
        if (it.isImg == 1) {
            val res = richImageDic.getOrElse(it.text.decodeUrl(), { R.color.transparent })
            appendSpannable(" ", CustomImageSpan(meiApplication().applicationContext, res))
        } else {
            val innerClick: ((String) -> Unit)? = if (click != null && it.action.isNotEmpty()) {
                { _ ->
                    if (it.afterClickAction.isNotEmpty()) {
                        //上报服务器用户点击了富文本的某点文字
                        if (BuildConfig.IS_TEST) UIToast.toast("上报服务器${it.afterClickAction} (beta版本提示一下)")
                        spiceHolder().apiSpiceMgr.executeKt(MessageClickRequest(it.afterClickAction))
                    }
                    click(it.action)
                }
            } else null
            appendLink(it.text.decodeUrl(), it.color.parseColor(defaultColor), it.fontScale,
                    it.deleteLine == 1, it.underline == 1, it.style,
                    it.backgroundColor.parseColor(0),verticalCenter = it.verticalCenter, click = innerClick)
        }
    }
    return this
}


fun List<SplitText>.createSplitSpannable(@ColorInt defaultColor: Int,
                                         click: ((action: String) -> Unit)? = null) =
        SpannableStringBuilder().appendSplitSpannable(this, defaultColor, click)