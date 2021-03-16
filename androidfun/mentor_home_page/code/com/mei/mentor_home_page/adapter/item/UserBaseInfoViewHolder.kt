package com.mei.mentor_home_page.adapter.item

import android.text.TextUtils
import android.view.View
import com.flyco.roundview.RoundTextView
import com.mei.live.views.ExpandableTextView.CardExpandableTextView
import com.mei.wood.R
import com.net.model.chick.friends.UserHomePagerResult
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout


/**
 *
 * @author Created by lenna on 2020/5/21
 */
class UserBaseInfoViewHolder(itemView: View) : MentorBaseHolder(itemView) {
    var etv: CardExpandableTextView? = null

    init {
        etv = getView(R.id.expand_text_view)

    }

    override fun refresh(item: Any) {
        val tagFlowLayout = getView<TagFlowLayout>(R.id.mentor_home_page_user_info_tag)
        if (item is UserHomePagerResult) {
            item.apply {
                info?.apply {
                    tagFlowLayout.adapter = getTagAdapter(publisherTags)
                    var duction: String? = introduction
                    if (TextUtils.isEmpty(duction)) {
                        duction = "亲爱的你是否有一些心事? 来跟我聊聊吧"
                    }
                    etv?.content = duction
                }

            }
        }

    }

    private fun getTagAdapter(info: List<String>): TagAdapter<String> {
        return object : TagAdapter<String>(info) {
            override fun getView(parent: FlowLayout?, position: Int, t: String): RoundTextView {
                val tagView = View.inflate(itemView.context, R.layout.item_user_skills_tag, null) as RoundTextView
                return tagView.also {
                    it.text = t
                }
            }
        }

    }
}