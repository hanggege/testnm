package com.mei.me.adapter

import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.util.glide.GlideDisPlayDefaultID
import com.mei.init.spiceHolder
import com.mei.orc.ext.selectBy
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.model.chick.friends.BlackListResult
import com.net.network.chick.friends.BlackChangeRequest

/**
 * author : Song Jian
 * date   : 2020/1/7
 * desc   : 黑名单列表
 */
class BlackListAdapter(val list: ArrayList<BlackListResult>) :
        BaseQuickAdapter<BlackListResult, BaseViewHolder>(R.layout.item_black_list, list) {


    override fun convert(holder: BaseViewHolder, item: BlackListResult) {
        //头像
        val avatar = holder.getView<RoundImageView>(R.id.black_list_avatar)
        val sex = holder.getView<TextView>(R.id.black_list_sex)
        val cancelBtn = holder.getView<TextView>(R.id.black_list_cancel)

        avatar.glideDisplay(
                item.avatar.orEmpty(),
                (item.gender == 1).selectBy(GlideDisPlayDefaultID.default_male_head, GlideDisPlayDefaultID.default_female_head)
        )
        holder.setText(R.id.black_list_name, item.nickname)
        holder.setText(R.id.black_list_time, "拉黑时间: ${item.blackTime}")
        //年龄
        sex.text = item.age.toString()
        //性别
        sex.setBackgroundResource((item.gender == 1).selectBy(R.drawable.icon_male_bg, R.drawable.icon_female_bg))

        cancelBtn.setOnClickListener {
            //解除拉黑
            spiceHolder().apiSpiceMgr.executeKt(BlackChangeRequest(item.userId, 1), success = {
                list.remove(item)
                notifyDataSetChanged()
            })
        }
    }

}