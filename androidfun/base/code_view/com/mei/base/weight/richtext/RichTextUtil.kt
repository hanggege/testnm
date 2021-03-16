package com.mei.base.weight.richtext

import android.webkit.URLUtil
import com.mei.base.weight.richtext.info.FindType
import com.mei.base.weight.richtext.info.RichInfo
import com.mei.base.weight.richtext.info.RichTag
import com.mei.base.weight.richtext.info.RichType
import com.mei.orc.imageload.cache.getImageWH
import com.mei.orc.john.upload.PhotoList
import com.mei.orc.util.string.stringConcate
import java.util.regex.Pattern

/**
 *
 * @author Created by Ling on 2019-07-09
 * 有关富文本的工具类
 */

private const val REGEX_IMG = RichTag.ImgStartTag + "(.*?)" + RichTag.ImgEndTag
private val PATTERN_IMG = Pattern.compile(REGEX_IMG)
private val PATTERN_TAG = Pattern.compile(RichTag.unKnowTag)

/**
 * 将本地url替换成网络url,
 * @param needPreLoadImgWH 是否需要提前加载图片的宽高
 */
fun replaceLocalPath(richTxt: String, photoList: PhotoList, needPreLoadImgWH: Boolean): String {
    val richTxtNew = replaceLocalPath(richTxt, photoList)
    if (needPreLoadImgWH) {
        preLoadImgWH(richTxtNew)
    }
    return richTxtNew
}

/**
 * 提取本地图片路径，将网络地址移除
 */
fun getLocationUrls(images: ArrayList<String>): ArrayList<String> {
    val httpUrls = arrayListOf<String>()
    try {
        images.forEach {
            if (URLUtil.isNetworkUrl(it)) {
                httpUrls.add(it)
            }
        }
        images.removeAll(httpUrls)
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return images
}


/**
 * 将本地url替换成网络url
 */
fun replaceLocalPath(richTxt: String, photoList: PhotoList): String {
    var resultTxt = richTxt
    photoList.forEach { photo ->
        resultTxt = resultTxt.replace(photo.localUri.orEmpty(), photo.uri.orEmpty())
    }
    return resultTxt
}


/**
 * 预加载图片宽高（用于富文本回显网络图片）
 */
private fun preLoadImgWH(draftStr: String) {
    // 提前设置
    val imgList = getListFromTxt(draftStr, FindType.IMG)
    imgList.forEach { info ->
        if (info.type === RichType.IMG && URLUtil.isNetworkUrl(info.text)) {
            info.text.getImageWH()
        }
    }
}

/**
 * 从带有图片和文字的富文本中提取文字部分
 * @param richTxt 带有标签的文本
 */
private fun getTextFromRichTxt(richTxt: String): String {
    var result = ""
    val list = getListFromTxt(richTxt, FindType.TXT)
    list.forEach {
        if (it.text.isNotEmpty()) {
            result = stringConcate(result, if (result.isNotEmpty()) "\n" else "", it.text)
        }
    }
    return result
}

private fun getListFromTxt(richTxt: String, type: FindType): ArrayList<RichInfo> {
    val result = arrayListOf<RichInfo>()
    if (richTxt.contains(RichTag.ImgStartTag) && richTxt.contains(RichTag.ImgEndTag)) {
        try {
            val matcher = PATTERN_IMG.matcher(richTxt)
            var index = 0
            while (matcher.find()) {
                val start = formatRichEdit(removeTag(richTxt.substring(index, matcher.start())))
                index = matcher.end()
                val url = matcher.group(1).orEmpty()

                if (url.isNotEmpty() && type === FindType.IMG) {
                    result.add(RichInfo(url, RichType.IMG))
                } else if (type === FindType.TXT) {
                    if (url.isNotEmpty()) {
                        result.add(RichInfo(start, RichType.TXT))
                    }
                } else {
                    if (start.isNotEmpty()) {
                        result.add(RichInfo(start, RichType.TXT))
                    }
                    if (url.isNotEmpty()) {
                        result.add(RichInfo(url, RichType.IMG))
                    }

                }
            }
            val endStr = formatRichEdit(removeTag(richTxt.substring(index)))
            if (endStr.isNotEmpty() && type !== FindType.IMG) {
                result.add(RichInfo(richTxt.substring(index), RichType.TXT))
            }
        } catch (e: Throwable) {
        }

    } else if (type !== FindType.IMG) {
        result.add(RichInfo(formatRichEdit(removeTag(richTxt)), RichType.TXT))
    }
    return result
}

/**
 * 去不认识的标签
 * @param richTxt 带有标签的文本
 */
private fun removeTag(richTxt: String): String {
    var result = richTxt
    val matcher = PATTERN_TAG.matcher(richTxt)
    while (matcher.find()) {
        if (!matcher.group().isNullOrEmpty()) {
            result = result.replace(matcher.group(), "")
        }
    }
    return result
}

/**
 * 转码
 */
fun formatRichEdit(richTxt: String): String {
    return richTxt.replace("&lt;".toRegex(), "<").replace("&gt;".toRegex(), ">")
}