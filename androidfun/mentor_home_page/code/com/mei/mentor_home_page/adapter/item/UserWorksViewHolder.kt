package com.mei.mentor_home_page.adapter.item

import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.czp.library.ArcProgress
import com.mei.mentor_home_page.inter.IUploadMeidiaType
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R
import com.net.model.chick.friends.ProductBean

/**
 *
 * @author Created by lenna on 2020/5/21
 * 作品item
 */
class UserWorksViewHolder(itemView: View) : MentorBaseHolder(itemView) {
    override fun refresh(item: Any) {
        if (item is ProductBean) {
            if (item.index % 2 == 0) {
                itemView.updatePadding(
                        right = itemView.dip(0.5f)
                )
            } else {
                itemView.updatePadding(
                        left = itemView.dip(0.5f)
                )
            }
            //播放按钮
            getView<ImageView>(R.id.product_play).apply {
                //后端刘彬说2.2.6  版本开始视频worksType改成4
                isVisible = 1 == item.worksType || 4 == item.worksType
            }

            //主图
            getView<ImageView>(R.id.product_show_img).apply {
                if (item.cover.orEmpty().isNotEmpty()) {
                    val url = if (URLUtil.isNetworkUrl(item.cover) && (item.worksType == 1 || item.worksType == 4)) {
                        item.cover
                    } else if (URLUtil.isNetworkUrl(item.cover) && item.worksType == 3) "${item.cover}?imageView2/1/w/$width/h/$height" else {
                        if (item.cover.isEmpty()) {
                            item.url
                        } else {
                            item.cover
                        }
                    }

                    glideDisplay(url)

                }
            }
            //置顶按钮
            setVisibleAndGone(R.id.product_top, 1 == item.isTop)

            loadProgress(item)

        }

    }

    private fun loadProgress(item: ProductBean) {
        setText(R.id.progress_text, "${item.progress.toInt()}%")
        getView<ArcProgress>(R.id.progress).apply {
            progress = item.progress.toInt()
        }
        val isVisible = item.progress.toInt() < 100
                && (item.progress.toInt() != -1)
                && item.worksType == IUploadMeidiaType.UPLOAD_TYPE_PRODUCT_VIDEO
        itemView.isEnabled = !(item.worksType == IUploadMeidiaType.UPLOAD_TYPE_PRODUCT_VIDEO && item.progress < 100 && item.progress > -1)
        setVisible(R.id.progress_container, isVisible)
    }

    fun refresh(item: Any, payloads: List<Any>) {
        if (payloads.isNotEmpty()) {
            if (item is ProductBean) {
                loadProgress(item)
            }
        }
    }

}