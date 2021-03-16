package com.mei.radio.adapter

import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.date.formatTimeDown
import com.mei.widget.living.LivingView
import com.mei.wood.R
import com.net.model.radio.RadioAudioInfo

/**
 * RadioFavoriteAdapter
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-22
 */
class RadioFavoriteAdapter(list: MutableList<RadioAudioInfo>) : BaseQuickAdapter<RadioAudioInfo, BaseViewHolder>(R.layout.item_favorite_audio, list) {
    var selectedPos = -1
    override fun convert(holder: BaseViewHolder, item: RadioAudioInfo) {
        val isSelected = selectedPos == holder.layoutPosition
        val serialNumView = holder.getView<TextView>(R.id.audio_serial_number).apply { text = item.serialNum.orEmpty() }
        val audioTitleView = holder.getView<TextView>(R.id.audio_title).apply {
            text = item.title.orEmpty()
            setTextColor(ContextCompat.getColor(context, if (isSelected) R.color.color_FF3A3A else R.color.color_333333))
        }
        val livingView = holder.getView<LivingView>(R.id.audio_living)
        holder.getView<TextView>(R.id.audio_duration).apply { text = (item.duration * TimeUnit.SECOND).formatTimeDown() }
        holder.itemView.isSelected = isSelected.apply {
            serialNumView.isInvisible = this
            audioTitleView.paint.isFakeBoldText = this
            livingView.isVisible = this
        }
    }
}