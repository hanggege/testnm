package com.mei.chat.ui.adapter.item.send.custom

import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.joker.im.Message
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.custom.chick.ServiceInfo
import com.mei.chat.ui.adapter.item.send.IMSendBaseMessageHolder
import com.mei.live.ext.createSplitSpannable
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.util.string.decodeUrl
import com.mei.widget.holder.setVisibleAndGone
import com.mei.wood.R
import com.mei.wood.ext.AmanLink
import com.mei.wood.util.MeiUtil
import com.mei.wood.web.MeiWebActivityLauncher
import com.mei.wood.web.MeiWebData
import kotlinx.android.synthetic.main.im_row_send_service_card_item.view.*

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/14
 */
class IMSendServiceCardMessageHolder(itemView: View) : IMSendBaseMessageHolder(itemView) {
    private var mTvPrice: TextView = itemView.findViewById(R.id.im_send_service_price_tv)
    private var mTvServiceName: TextView = itemView.findViewById(R.id.im_send_service_name_tv)
    private var mTvHint: TextView = itemView.findViewById(R.id.im_send_specification_tv)
    private var mTvCount: TextView = itemView.findViewById(R.id.im_send_service_count_tv)


    @SuppressLint("ClickableViewAccessibility")
    override fun refreshChild(msg: Message) {
        itemView.apply {
            val data = getCustomData()?.data as? ChickCustomData
            if (data?.serviceInfo == null) {
                im_send_service_unit.isVisible = false
                unKnownMessageLoad()
            } else {
                data.serviceInfo?.let {
                    when (it.cardType) {

                        ServiceInfo.CardType.TYPE_CANCEL -> {
                            val hint = String.format(resources.getString(R.string.time_remaining_text), it.timeRemaining)
                            val count = String.format(resources.getString(R.string.up_count_count_text), it.upCount)
                            mTvServiceName.text = it.serviceName.decodeUrl()
                            mTvPrice.isVisible = false
                            mTvHint.text = hint
                            mTvHint.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                leftMargin = 0
                            }
                            mTvCount.text = count
                            mTvCount.isVisible = true
                            im_send_service_unit.isVisible = false
                            setVisibleAndGone(R.id.history_service_cl, true)
                            setVisibleAndGone(R.id.price_text_tv, false)
                        }
                        ServiceInfo.CardType.TYPE_ASK,
                        ServiceInfo.CardType.TYPE_NORMAL,
                        ServiceInfo.CardType.TYPE_RECOMMEND,
                        ServiceInfo.CardType.TYPE_RECHARGE -> {
                            mTvPrice.isVisible = true
                            mTvServiceName.text = it.serviceName.decodeUrl()
                            mTvPrice.text = "${it.serviceCost}"

                            val hint = String.format(resources.getString(R.string.specification_text), it.serviceMinutes)
                            val count = String.format(resources.getString(R.string.service_count_text), it.serviceMin)
                            mTvHint.text = hint
                            mTvHint.updateLayoutParams<ConstraintLayout.LayoutParams> {
                                leftMargin = dip(5)
                            }
                            mTvCount.text = count
                            mTvCount.isVisible = it.serviceMin > 1

                            im_send_service_unit.isVisible = true

                            setVisibleAndGone(R.id.history_service_cl, it.priceText?.isNotEmpty() != true)
                            setVisibleAndGone(R.id.price_text_tv, it.priceText?.isNotEmpty() == true)
                            setText(R.id.price_text_tv, it.priceText.orEmpty().createSplitSpannable(Cxt.getColor(R.color.color_333333)))

                        }
                        else -> {
                            im_send_service_unit.isVisible = false
                            unKnownMessageLoad()
                        }
                    }
                    im_message_container.setOnClickListener { view ->
                        goToServiceDetailPage(view.context, it)
                    }
                }


            }
        }
    }

    /**
     * 未知消息
     */
    private fun unKnownMessageLoad() {
        mTvServiceName.text = "不支持该类型消息请升级高版本"
        mTvPrice.isVisible = false
        mTvHint.isVisible = false
        mTvCount.isVisible = false
    }


    private fun goToServiceDetailPage(context: Context, serviceAd: ServiceInfo?) {
        MeiWebActivityLauncher.startActivity(context
                , MeiWebData(MeiUtil.appendParamsUrl(AmanLink.NetUrl.exclusive_service_details, "service_id" to "${serviceAd?.specialServiceId}", "from" to "im")
                , 0).apply {
            need_reload_web = 1
        })
    }


}