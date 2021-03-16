package com.mei.chat.ui.adapter.item.receive

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ClickableSpan
import android.text.style.URLSpan
import android.view.View
import android.widget.TextView
import com.joker.im.Message
import com.mei.base.ui.nav.Nav
import com.mei.orc.util.click.ClickMovementMethod
import com.mei.widget.holder.addOnLongClickListener
import com.mei.wood.R
import com.vdurmont.emoji.EmojiParser

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMReceiveTextMessageHolder(itemView: View) : IMReceiveBaseMessageHolder(itemView) {
    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        addOnLongClickListener(R.id.im_msg_content)
        val contentTextView = getView<TextView>(R.id.im_msg_content)
        contentTextView.setOnTouchListener(ClickMovementMethod.newInstance())
        contentTextView.text = ""
        contentTextView.makeCustomLink(msg.getSummary())
    }
}


fun TextView.makeCustomLink(input: String) {
    setText(EmojiParser.parseToUnicode(input), TextView.BufferType.SPANNABLE)
    (text as? Spannable)?.apply {
        val urls = getSpans(0, length, URLSpan::class.java)
        val spanBuilder = SpannableStringBuilder(this)
        spanBuilder.clearSpans()
        urls.forEach {
            val urlSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    Nav.toWebActivity(widget.context, it.url, "")
                }
            }
            spanBuilder.setSpan(urlSpan, getSpanStart(it), getSpanEnd(it), Spannable.SPAN_EXCLUSIVE_INCLUSIVE)
        }
        setText(spanBuilder, TextView.BufferType.SPANNABLE)

    }
}