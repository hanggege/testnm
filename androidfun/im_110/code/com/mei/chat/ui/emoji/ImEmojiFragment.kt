package com.mei.chat.ui.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.mei.wood.R
import com.mei.wood.ext.loadEmojis
import com.mei.wood.ui.CustomSupportFragment
import com.rockerhieu.emojicon.emoji.Emojicon
import kotlinx.android.synthetic.main.im_emoji_fragment.*
import java.util.*


/**
 * Created by zzw on 2019/3/15
 * Des: 表情
 */
class ImEmojiFragment : CustomSupportFragment() {

    var inputEmoji: (String) -> Unit = {}
    var deleteEdit: () -> Unit = {}

    //五排 每一排7个 最后一个是回退
    private val pageNum = 5 * 7 - 1
    private val emojiList: MutableList<Array<Emojicon>> by lazy {
        arrayListOf<Array<Emojicon>>().apply {
            val allList = loadEmojis()
            val pages = allList.size / pageNum + if (allList.size % pageNum > 0) 1 else 0
            for (i in 0 until pages) {
                val element = Arrays.copyOfRange<Emojicon>(allList, i * pageNum, Math.min((i + 1) * pageNum, allList.size))
                this.add(element.plus(Emojicon(EMOJI_BACKSPACE)))
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.im_emoji_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val fragments = mutableListOf<ImEmojiPageFragment>()

        for (value in emojiList) {
            //设置数据
            fragments.add(ImEmojiPageFragment().apply {
                data = value.toMutableList()
                inputEmoji = this@ImEmojiFragment.inputEmoji
                deleteEdit = this@ImEmojiFragment.deleteEdit
            })
        }
        emoki_page_indicator_view.buildIndicator(fragments.size)
        if (emojiList.size == 1) {
            emoki_page_indicator_view.visibility = View.GONE
        } else {
            emoki_page_indicator_view.visibility = View.VISIBLE
        }

        emoji_pager2.adapter = EmojisPagerAdapter(this, fragments)

        emoji_pager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                emoki_page_indicator_view.selectIndex(position)
            }
        })
        emoji_pager2.forEach {
            if (it is RecyclerView) {
                it.overScrollMode = View.OVER_SCROLL_NEVER
            }
        }
    }


    private class EmojisPagerAdapter(fm: Fragment, private val fragments: List<Fragment>)
        : FragmentStateAdapter(fm) {
        override fun createFragment(position: Int): Fragment = fragments[position]

        override fun getItemCount() = fragments.size
    }
}
