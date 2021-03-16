package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.wood.R
import com.net.model.user.GroupInfo

/**
 * Created by hang on 2020/7/27.
 */
class CardStudioHolder(view: View) : CardBaseHolder(view) {

    init {
        getView<TextView>(R.id.studio_name).paint.isFakeBoldText = true
    }

    override fun refresh(item: Any) {
        (item as? GroupInfo)?.let {
            getView<ImageView>(R.id.studio_cover).glideDisplay(it.avatar.orEmpty(), R.drawable.default_studio_cover)
            setText(R.id.studio_name, it.groupName.orEmpty())
        }
    }
}