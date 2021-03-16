package com.mei.live.ui.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import com.joker.im.custom.chick.Extra
import com.mei.dialog.PayFromType
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.wood.R
import com.net.model.chick.pay.ProductBean
import kotlinx.android.synthetic.main.dialog_apply_upstream_recharge.*

/**
 * Created by hang on 2020/6/15.
 */

fun FragmentActivity.showApplyUpstreamRechargeDialog(products: List<ProductBean>, extra: Extra? = null, selectPosition: Int, fromType: PayFromType, back: (Int, ProductBean) -> Unit = { _, _ -> }) {
    ApplyUpstreamRechargeDialog().apply {
        mProducts = products
        mSelectPosition = selectPosition
        mBack = back
        mFromType = fromType
        mExtra = extra
    }.show(supportFragmentManager, ApplyUpstreamRechargeDialog::class.java.simpleName)
}

class ApplyUpstreamRechargeDialog : BottomDialogFragment() {

    var mProducts: List<ProductBean>? = null

    var mSelectPosition = -1

    var mBack: (Int, ProductBean) -> Unit = { _, _ -> }
    var mFromType: PayFromType = PayFromType.UP_APPLY
    var mExtra: Extra? = null
    override fun onSetInflaterLayout() = R.layout.dialog_apply_upstream_recharge

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        apply_upstream_recharge_view.notifyData(mProducts.orEmpty(), mExtra, mSelectPosition, mFromType) { back, product ->
            if (back == 1) {
                dismissAllowingStateLoss()
            }
            mBack(back, product)
        }
    }
}