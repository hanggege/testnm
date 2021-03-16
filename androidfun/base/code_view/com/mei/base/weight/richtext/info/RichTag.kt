package com.mei.base.weight.richtext.info

/**
 * Created by joker on 16/9/5.
 */
object RichTag {
    const val ImgStartTag = "<img src=\""
    const val ImgEndTag = "\">"
    const val unKnowTag = "<([^>|^<]*)>"

    const val boldStartTag = "<strong>"
    const val boldEndTag = "</strong>"

    const val italicStartTag = "<em>"
    const val italicEndTag = "</em>"

    const val underLineStartTag = "<u>"
    const val underLineEndTag = "</u>"

    const val linkStartTag = "<a>"
    const val linkEndTag = "</a>"
}
