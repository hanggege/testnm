package com.mei.wood.ext

import com.mei.orc.util.string.readRaw2Obj
import com.mei.wood.R
import com.rockerhieu.emojicon.emoji.Emojicon
import com.rockerhieu.emojicon.emoji.EmojiconData
import java.util.*

/**
 * Created by joker on 2017/11/29.
 */
/**
 * 获取emoji
 */
fun loadEmojis(): Array<Emojicon> = loadEmojis2().toTypedArray()

fun loadEmojis2(): ArrayList<Emojicon> {
    val list = ArrayList<Emojicon>()
    if (EmojiconData.shareEmoji == null || EmojiconData.shareEmoji.list.isNullOrEmpty()) {
        EmojiconData.shareEmoji = readRaw2Obj(R.raw.emojis, EmojiconData::class.java)
    }

    if (EmojiconData.shareEmoji != null && EmojiconData.shareEmoji.list.orEmpty().isNotEmpty()) {
        EmojiconData.shareEmoji.list.forEach {
            val sb = StringBuilder()
            for (c in Character.toChars(Integer.parseInt(it))) {
                sb.append(c)
            }
            list.add(Emojicon(sb.toString()))
        }
    }
    return list
}