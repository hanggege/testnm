package com.mei.chat.ui.adapter.item.receive

import android.view.View
import android.widget.ImageView
import androidx.core.view.updateLayoutParams
import com.joker.im.Message
import com.joker.im.message.ImageMessage
import com.mei.base.ui.nav.Nav
import com.mei.browser.BrowserInfo
import com.mei.orc.ext.dip
import com.mei.widget.holder.quickAdapter
import com.mei.wood.GlideApp
import com.mei.wood.R
import com.tencent.imsdk.TIMImageType
import kotlin.math.max

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMReceivePictureMessageHolder(itemView: View) : IMReceiveBaseMessageHolder(itemView) {
    override fun refreshChild(msg: Message) {
        (msg as? ImageMessage)?.let {
            pictureLoad(it, itemView)

            itemView.findViewById<View>(R.id.im_message_container)?.setOnClickListener {
                /** 查看列表中的所有图片，选中当前点击这条 **/
                val imageList = arrayListOf<ImageMessage>()
                for (index in 0..(quickAdapter?.itemCount ?: 0)) {
                    val item = quickAdapter?.getItemOrNull(index)
                    if (item is ImageMessage) imageList.add(item)
                }
                var initIndex = imageList.indexOfFirst { image -> image.timMessage.msgUniqueId == message?.timMessage?.msgUniqueId }
                if (initIndex < 0) initIndex = 0
                Nav.toViewImages(itemView.context, BrowserInfo().apply {
                    index = initIndex
                    images = imageList.map { image -> image.getImageUrl() }
                })
            }
        }
    }


    private fun pictureLoad(msg: ImageMessage, itemView: View) {
        val displayImageView = itemView.findViewById<ImageView>(R.id.im_msg_picture)
        val imageElem = msg.getImageElem()
        imageElem?.apply {
            val maxWH = displayImageView.dip(140).toLong()
            val minWH = displayImageView.dip(50).toLong()
            val wh: Pair<Long, Long> = if (width >= height && width > maxWH) {
                Pair(maxWH, height * maxWH / width)
            } else if (height >= width && height > maxWH) {
                Pair(width * maxWH / height, maxWH)
            } else if (width < minWH && height < minWH) {
                Pair(minWH, minWH)
            } else {
                Pair(width, height)
            }
            displayImageView.updateLayoutParams {
                width = max(wh.first, minWH).toInt()
                height = max(wh.second, minWH).toInt()
            }
        }

        GlideApp.with(displayImageView)
                .load(msg.getImageUrl(TIMImageType.Thumb))
                .placeholder(R.color.color_ebebeb)
                .error(R.color.color_ebebeb)
                .into(displayImageView)

    }
}