package com.mei.chat.ui.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.wood.R
import com.mei.wood.ui.CustomSupportFragment
import com.rockerhieu.emojicon.EmojiconTextView
import com.rockerhieu.emojicon.emoji.Emojicon
import kotlinx.android.synthetic.main.im_emoji_page_fragment.*

/**
 *  Created by zzw on 2019/3/15
 *  Des: 每一页表情
 */

//空表情
const val EMOJI_EMPTY = "empty"

//返回按钮
const val EMOJI_BACKSPACE = "backspace"

class ImEmojiPageFragment : CustomSupportFragment() {
    var data: MutableList<Emojicon> = arrayListOf()
    var inputEmoji: (String) -> Unit = {}
    var deleteEdit: () -> Unit = {}


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.im_emoji_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        im_emoji_page_recy.layoutManager = GridLayoutManager(context, 7)
        im_emoji_page_recy.adapter = EmojiAdapter(data).apply {
            setOnItemClickListener { _, _, position ->
                val emojicon = data[position]
                when (emojicon.emoji) {
                    EMOJI_BACKSPACE -> {
                        deleteEdit()
                    }
                    else -> {
                        inputEmoji(emojicon.emoji)
                    }
                }
            }
        }
    }


    class EmojiAdapter(list: MutableList<Emojicon>) : BaseQuickAdapter<Emojicon, BaseViewHolder>(R.layout.im_emoji_item, list) {
        override fun convert(holder: BaseViewHolder, item: Emojicon) {
            item.apply {
                when (emoji) {
                    null,
                    EMOJI_EMPTY -> {
                        holder.itemView.visibility = View.GONE
                    }
                    EMOJI_BACKSPACE -> {
                        holder.itemView.visibility = View.VISIBLE

                        holder.setVisible(R.id.emojicon_icon, false)
                        holder.setVisible(R.id.enojicn_iv, true)
                        holder.setImageResource(R.id.enojicn_iv, R.drawable.im_delete_expression)
                    }
                    else -> {
                        holder.itemView.visibility = View.VISIBLE

                        holder.setVisible(R.id.emojicon_icon, true)
                        val emojicon = holder.getView<EmojiconTextView>(R.id.emojicon_icon)
                        emojicon.text = emoji
                        holder.setVisible(R.id.enojicn_iv, false)
                    }
                }
            }
        }
    }


}