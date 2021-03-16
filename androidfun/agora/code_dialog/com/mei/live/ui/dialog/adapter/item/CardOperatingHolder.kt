package com.mei.live.ui.dialog.adapter.item

import android.view.View
import android.widget.TextView
import com.mei.live.ui.dialog.linstener.IOperatingListener
import com.mei.wood.R

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog.adapter.item
 * @ClassName:      CardIntroductionHolder
 * @Description:    操作 送礼，关注等
 * @Author:         zxj
 * @CreateDate:     2020/6/10 17:32
 * @UpdateUser:
 * @UpdateDate:     2020/6/10 17:32
 * @UpdateRemark:
 * @Version:
 */
class CardOperatingHolder(itemV: View) : CardBaseHolder(itemV) {
    var iOperatingListener: IOperatingListener? = null

    override fun refresh(item: Any) {
        setText(R.id.tv_card_dialog_to_im, "@TA")

        getView<TextView>(R.id.tv_card_dialog_to_im).setOnClickListener {
            iOperatingListener?.setAT()
        }
        getView<TextView>(R.id.tv_card_dialog_send_gift).setOnClickListener {
            iOperatingListener?.setGift()
        }
        getView<TextView>(R.id.tv_item_card_follow_anchor).apply {
            if (item is OperatingType) {
                if (item.isFocus) {
                    //已经关注
                    text = "私聊"
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.icon_chat_tip, 0, 0, 0)
                } else {
                    //没有关注
                    text = "关注"
                    setCompoundDrawablesRelativeWithIntrinsicBounds(
                            R.drawable.icon_follow_add, 0, 0, 0)
                }
                setOnClickListener {
                    if (item.isFocus) iOperatingListener?.setIM() else iOperatingListener?.setFocus()
                }
            }
        }
    }


}

class OperatingType {
    var isFocus = false

}