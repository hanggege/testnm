package com.mei.me.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.orc.ext.selectBy
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.net.model.chick.friends.BlackListResult

/**
 * author : Song Jian
 * date   : 2020/1/19
 * desc   : 守护列表
 */
class GuardianListAdapter(val list: ArrayList<BlackListResult>) :
        BaseQuickAdapter<BlackListResult, BaseViewHolder>(R.layout.item_guardian_list, list) {


    override fun convert(holder: BaseViewHolder, item: BlackListResult) {
        //排名
        holder.getView<RoundImageView>(R.id.guardian_list_sort).apply {

        }
        //头像
        holder.getView<RoundImageView>(R.id.guardian_list_avatar).apply {
            glideDisplay(
                    item.avatar.orEmpty(),
                    (item.gender == 1).selectBy(GlideDisPlayDefaultID.default_male_head, GlideDisPlayDefaultID.default_female_head)
            )
        }
        //昵称
        holder.getView<TextView>(R.id.guardian_list_name).apply {
            text = item.nickname
        }
        //性别
        holder.getView<TextView>(R.id.guardian_list_sex).apply {
            text = item.age.toString()
            setBackgroundResource((item.gender == 1).selectBy(R.drawable.icon_male_bg, R.drawable.icon_female_bg))
        }
        //心币数量
        holder.getView<TextView>(R.id.guardian_list_gift_count).apply {

        }
    }

}