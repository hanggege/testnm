package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.updateLayoutParams
import com.flyco.roundview.RoundTextView
import com.mei.live.views.ExpandableTextView.CardExpandableTextView
import com.mei.orc.ext.dip
import com.mei.orc.util.string.replaceBlank
import com.mei.wood.R
import com.zhy.view.flowlayout.FlowLayout
import com.zhy.view.flowlayout.TagAdapter
import com.zhy.view.flowlayout.TagFlowLayout

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.adapter.item
 * @ClassName:      CardIntroductionHolder
 * @Description:    个人信息和标签
 * @Author:         zxj
 * @CreateDate:     2020/6/10 17:32
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 17:32
 * @UpdateRemark:
 * @Version:
 */
class CardIntroductionHolder(itemV: View) : CardBaseHolder(itemV) {
    var etv: CardExpandableTextView? = null

    init {
        etv = getView(R.id.expand_text_card_dialog_view)

    }

    override fun refresh(item: Any) {
        if (item is CardIntroductionData) {
            etv?.apply {
                item.introduction?.let {
                    content = if (item.needExpand) {
                        it.replaceBlank()
                    } else {
                        it
                    }
                }
            }

            item.publisherTags?.let {
                getView<TagFlowLayout>(R.id.id_flowlayout_card_dialog).apply {
                    adapter = getTagAdapter(it)
                    updateLayoutParams<LinearLayout.LayoutParams> {
                        setMargins(dip(15), if (item.isGroup) 0 else if (item.isSelf) dip(35) else dip(15), dip(15), 0)
                    }

                }
            }
        }

    }

    private fun getTagAdapter(info: List<String>): TagAdapter<String> {
        return object : TagAdapter<String>(info) {
            override fun getView(parent: FlowLayout?, position: Int, t: String): RoundTextView {
                val tagView = View.inflate(itemView.context, R.layout.item_card_dialog_lable_holder, null) as RoundTextView
                return tagView.also {
                    it.text = t
                }
            }
        }

    }

    class CardIntroductionData {
        /**
         * 简介
         */
        var introduction: String? = null

        /**
         * 主播标签
         */
        var publisherTags: List<String>? = null

        /**
         * 是否需要展示
         */
        var needExpand = false

        /**
         * 是否自己看自己
         */
        var isSelf = false

        /**
         * 是否是工作室
         */
        var isGroup = false
    }
}