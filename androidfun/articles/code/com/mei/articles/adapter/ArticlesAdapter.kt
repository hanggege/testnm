package com.mei.articles.adapter

import android.view.ViewGroup
import android.widget.LinearLayout
import com.chad.library.adapter.base.BaseMultiItemQuickAdapter
import com.mei.articles.adapter.holder.ArticlesItemHolder
import com.mei.articles.adapter.holder.ArticlesTitleHolder
import com.mei.base.baseadapter.multi.MultiItemType
import com.mei.base.baseadapter.multi.MultipleItem
import com.mei.orc.ext.dip
import com.mei.orc.ext.layoutInflaterKt
import com.mei.wood.R
import com.mei.wood.ui.holder.BaseHomeHolder
import com.mei.wood.ui.holder.EmptyHolder

/**
 *  Created by zzw on 2019-12-20
 *  Des:
 */

val ITEM_TYPE_TITLE = MultiItemType.ItemType1 // 标题栏
val ITEM_TYPE_ELITE_1 = MultiItemType.ItemType2//文章Item
val ITEM_TYPE_ELITE_2 = MultiItemType.ItemType3//文章Item

class ArticlesAdapter(listData: MutableList<MultipleItem<*>>) : BaseMultiItemQuickAdapter<MultipleItem<*>, BaseHomeHolder>(listData) {

    override fun getDefItemViewType(position: Int): Int {
        return getItemOrNull(position)?.itemType ?: super.getDefItemViewType(position)
    }

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseHomeHolder {
        return when (viewType) {
            /**标题栏*/
            ITEM_TYPE_TITLE.type -> ArticlesTitleHolder(context.layoutInflaterKt(R.layout.item_articles_title, parent))
            /**文章Item*/
            ITEM_TYPE_ELITE_1.type -> ArticlesItemHolder(context.layoutInflaterKt(R.layout.item_articles_image_left, parent))
            ITEM_TYPE_ELITE_2.type -> ArticlesItemHolder(context.layoutInflaterKt(R.layout.item_articles_image_right, parent))

            else -> EmptyHolder(LinearLayout(parent.context).apply { layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, dip(30)) })
        }
    }


    override fun convert(holder: BaseHomeHolder, item: MultipleItem<*>) {
        holder.refresh(item.data)
    }

}

