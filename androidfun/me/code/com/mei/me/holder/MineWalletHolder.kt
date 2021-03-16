package com.mei.me.holder

import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.mei.base.baseadapter.multi.MultiViewHolder
import com.mei.login.checkLogin
import com.mei.me.activity.MyRoseActivity
import com.mei.orc.ext.selectBy
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import com.net.model.user.MyPageInfo

/**
 * Created by hang on 2020/7/24.
 */
class MineWalletHolder(view: View) : MultiViewHolder(view) {

    init {
        getView<ImageView>(R.id.top_bg).setOnClickNotDoubleListener {
            if (itemView.context.checkLogin()) {
                itemView.context.startActivity(Intent(itemView.context, MyRoseActivity::class.java))
            }
        }

        getView<TextView>(R.id.balance_count).setOnClickNotDoubleListener {
            if (itemView.context.checkLogin()) {
                MeiWebActivityLauncher.startActivity(itemView.context, MeiWebData(AmanLink.NetUrl.wallet_index, 0).apply {
                    need_reload_web = 1
                })
            }
        }
        getView<TextView>(R.id.mine_wallet).setOnClickNotDoubleListener {
            if (itemView.context.checkLogin()) {
                MeiWebActivityLauncher.startActivity(itemView.context, MeiWebData(AmanLink.NetUrl.wallet_index, 0).apply {
                    need_reload_web = 1
                })
            }
        }
    }

    override fun refresh(data: Any?) {
        (data as? MyPageInfo.Finance)?.let { finance ->
            setText(R.id.mine_coin_text, (finance.coin?.coinBalance ?: 0).toString())
            setVisible(R.id.top_up, finance.wallet == null || !JohnUser.getSharedUser().hasLogin())
            setVisible(R.id.group_mine_wallet, finance.wallet != null)
            setText(R.id.balance_count, (finance.wallet?.moneyBalance ?: 0.00 == 0.00).selectBy("0", changTVSize("${finance.wallet?.moneyBalance ?: 0}元")))
        }
    }
}

/**
 * 小数点前后大小不一致 效果
 */
private fun changTVSize(value: String): CharSequence? {
    return SpannableString(value).apply {
        if (contains(".")) {
            setSpan(RelativeSizeSpan(0.4f), value.indexOf("."), value.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
}