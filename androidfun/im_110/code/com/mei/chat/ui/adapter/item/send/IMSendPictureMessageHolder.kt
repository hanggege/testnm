package com.mei.chat.ui.adapter.item.send

import android.graphics.BitmapFactory
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
import java.io.File
import kotlin.math.max

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendPictureMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
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
                var initIndex = imageList.indexOfFirst { it.timMessage.msgUniqueId == message?.timMessage?.msgUniqueId }
                if (initIndex < 0) initIndex = 0
                Nav.toViewImages(itemView.context, BrowserInfo().apply {
                    index = initIndex
                    images = imageList.map { it.getImageUrl() }
                    downloadable = true
                })
            }
        }
    }

    /** 计算图片宽高去设置大小  **/
    private fun pictureLoad(msg: ImageMessage, itemView: View) {
        val displayImageView = itemView.findViewById<ImageView>(R.id.im_msg_picture)

        val maxWH = displayImageView.dip(140).toLong()
        val minWH = displayImageView.dip(50).toLong()

        var bitmapWH: Pair<Long, Long> = Pair(maxWH, maxWH)
        val imageElem = msg.getImageElem()
        if (imageElem != null) {
            bitmapWH = Pair(imageElem.width, imageElem.height)
        } else if (File(msg.getImageUrl()).exists()) {
            val bitmap = BitmapFactory.decodeFile(msg.getImageUrl())
            if (bitmap != null && bitmap.rowBytes > 0) {
                bitmapWH = Pair(bitmap.width.toLong(), bitmap.height.toLong())
            }
        }

        val bWidth = bitmapWH.first
        val bHeight = bitmapWH.second
        val wh: Pair<Long, Long> = if (bWidth >= bHeight && bWidth > maxWH) {
            Pair(maxWH, bHeight * maxWH / bWidth)
        } else if (bHeight >= bWidth && bHeight > maxWH) {
            Pair(bWidth * maxWH / bHeight, maxWH)
        } else if (bWidth <= minWH && bHeight <= minWH) {
            Pair(minWH, minWH)
        } else Pair(bWidth, bHeight)

        displayImageView.updateLayoutParams {
            width = max(wh.first, minWH).toInt()
            height = max(wh.second, minWH).toInt()
        }

        GlideApp.with(displayImageView)
                .load(msg.getImageUrl(TIMImageType.Thumb))
                .placeholder(R.color.color_ebebeb)
                .error(R.color.color_ebebeb)
                .into(displayImageView)

    }
}