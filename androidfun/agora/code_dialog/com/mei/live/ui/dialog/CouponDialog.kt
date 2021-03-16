package com.mei.live.ui.dialog

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.text.buildSpannedString
import androidx.core.view.updateLayoutParams
import com.joker.im.custom.chick.SplitText
import com.mei.base.common.COUPON_TO_SERVICE
import com.mei.base.ui.nav.Nav
import com.mei.live.ext.parseColor
import com.mei.live.ui.dialog.adapter.DialogCouponAdapter
import com.mei.orc.Cxt
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.chick.coupon.CouponListInfo
import com.net.network.chick.coupon.CouponListRequest
import kotlinx.android.synthetic.main.dialog_coupon.*

/**
 * CouponDialog
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-09
 */
fun MeiCustomBarActivity.showCouponDialog(couponId: Int, couponNum: String, publisherId: Int = -1, toBottomFragment: Int = 0) {
    showLoading(true)
    apiSpiceMgr.executeToastKt(CouponListRequest(couponId, couponNum, publisherId), success = {
        if (it.isSuccess) {
            it.data?.apply {
                CouponDialog().apply {
                    limitTime = limitTimeText
                    couponList.addAll(targets.orEmpty())
                    discount = discountText
                    this.toBottomFragment = toBottomFragment == 1
                }.show(supportFragmentManager, CouponDialog::class.java.simpleName)
            }
        }
    }, finish = {
        showLoading(false)
    })
}

class CouponDialog : MeiSupportDialogFragment() {

    var limitTime: SplitText = SplitText()
    var discount: SplitText = SplitText()
    var toBottomFragment = false
    val couponList = arrayListOf<CouponListInfo.CouponListLabelInfo>()
    private val adapter by lazy {
        DialogCouponAdapter(couponList).apply {
            setOnItemClickListener { _, _, position ->
                if (toBottomFragment) {
                    postAction(COUPON_TO_SERVICE, couponList[position].resourceId)
                } else {
                    Nav.toAmanLink(context, couponList[position].action)
                }
                dialog?.dismiss()
            }
        }
    }

    override fun onSetInflaterLayout(): Int = R.layout.dialog_coupon

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        view?.updateLayoutParams {
            width = dip(320)
            height = if (couponList.size > 2) dip(402) else WindowManager.LayoutParams.WRAP_CONTENT
        }
        dialog_coupon_rv.adapter = adapter
        dialog_coupon_exit.setOnClickListener {
            dialog?.dismiss()
        }
        dialog_coupon_label_discount.text = buildSpannedString {
            appendLink(discount.text, discount.color.parseColor(Cxt.getColor(R.color.color_333333)))
        }
        dialog_coupon_label_deadline.text = buildSpannedString {
            appendLink(limitTime.text, limitTime.color.parseColor(Cxt.getColor(R.color.color_333333)))
        }
    }
}