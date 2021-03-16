package com.mei.work.adapter.item

import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.widget.round.RoundView
import com.mei.wood.R
import com.net.model.chick.workroom.WorkRoomMember

/**
 *
 * @author Created by lenna on 2020/7/30
 * 工作室成员信息编辑Item
 */
class MemberListViewHolder(itemView: View) : BaseViewHolder(itemView) {
    fun refreshItem(member: WorkRoomMember?, position: Int) {
        getView<RoundImageView>(R.id.avatar_riv).glideDisplay(member?.avatar.orEmpty(), R.drawable.default_avatar_50, R.drawable.default_avatar_50)
        setText(R.id.member_name_tv, member?.nickname)
        getView<TextView>(R.id.member_name_tv).setTextColor(if (member?.isChecked == true) Cxt.getColor(R.color.color_333333) else Cxt.getColor(R.color.color_666666))
        getView<RoundView>(R.id.checked_rv).setBackColor(if (member?.isChecked == true) Cxt.getColor(R.color.color_333333) else Cxt.getColor(android.R.color.white))
        getView<TextView>(R.id.member_name_tv).paint.isFakeBoldText = member?.isChecked == true
        if (position == 0) {
            getView<RoundImageView>(R.id.avatar_riv)
                    .updateLayoutParams<ConstraintLayout.LayoutParams> {
                        marginStart = itemView.dip(15)
                    }
        } else {
            getView<RoundImageView>(R.id.avatar_riv)
                    .updateLayoutParams<ConstraintLayout.LayoutParams> {
                        marginStart = itemView.dip(7)
                    }
        }
    }
}