package com.mei.articles.adapter.holder

import android.view.View
import android.widget.ImageView
import com.mei.base.ui.nav.Nav
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.ui.holder.BaseHomeHolder
import com.mei.wood.util.MeiUtil.appendParamsUrl
import com.net.model.chick.room.HandPickResult

/**
 *  Created by zzw on 2019-12-20
 *  Des:
 */

class ArticlesItemHolder(view: View) : BaseHomeHolder(view) {

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun refresh(data: Any) {
        if (data is HandPickResult.Elite_cate_item) {
            val eliteImg = getView<ImageView>(R.id.item_articles_img)
            eliteImg.glideDisplay(data.image, GlideDisPlayDefaultID.default_recommend_elite)

            setText(R.id.item_articles_title, data.title)
            setText(R.id.item_articles_time, data.sdate)
            //影藏时间
            setVisibleAndGone(R.id.item_articles_time, false)
            setText(R.id.item_articles_tag, data.keywords)

            setVisibleAndGone(R.id.item_articles_tag, !data.keywords.isNullOrEmpty())

            itemView.setOnClickListener {
                val url = appendParamsUrl(AmanLink.NetUrl.article_info_url, Pair("feed_id", data.feed_id))
                Nav.toWebActivity(itemView.context, url)
            }
        }
    }


}