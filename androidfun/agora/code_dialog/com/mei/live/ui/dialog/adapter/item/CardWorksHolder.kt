package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.webkit.URLUtil
import android.widget.ImageView
import androidx.core.view.isVisible
import com.flyco.roundview.RoundTextView
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.chick.friends.ProductBean

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.adapter.item
 * @ClassName:      CardIntroductionHolder
 * @Description:    作品
 * @Author:         zxj
 * @CreateDate:     2020/6/10 17:32
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 17:32
 * @UpdateRemark:
 * @Version:
 */
class CardWorksHolder(itemV: View) : CardBaseHolder(itemV) {
    override fun refresh(item: Any) {
        if (item is ProductBean) {

            //播放按钮
            getView<ImageView>(R.id.product_play).apply {
                isVisible = 1 == item.worksType
            }

            //主图
            getView<ImageView>(R.id.product_show_img).apply {
                val url = if (URLUtil.isNetworkUrl(item.cover) && item.worksType == 1 || item.worksType == 4) item.cover
                else if (URLUtil.isNetworkUrl(item.cover) && item.worksType == 3) "${item.cover}?imageView2/1/w/$width/h/$height" else item.cover
                glideDisplay(url)
            }
            //置顶按钮
            getView<RoundTextView>(R.id.product_top).apply {
                isVisible = 1 == item.isTop
            }
        }

    }

}