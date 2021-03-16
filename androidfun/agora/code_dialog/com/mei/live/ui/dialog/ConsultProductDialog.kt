package com.mei.live.ui.dialog

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.mei.GrowingUtil
import com.mei.live.ui.dialog.adapter.ProductAdapter
import com.mei.live.ui.dialog.adapter.QuickConsultItemDecoration
import com.mei.orc.ext.layoutInflaterKt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.room.ProductCategory
import com.net.network.room.ProductCategoryRequest

/**
 * ConsultDirectionDialog
 *
 * 快速咨询中咨询方向弹窗
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-10-13
 * @param couponNum 使用优惠券 但是不指定使用那一张则传-1, 具体使用那一张则传具体优惠券的编号. 0 表示不使用优惠券
 */
fun MeiCustomBarActivity.showConsultDirectionDialog(
        couponNum: Long = 0,
        userStatus: Int = 0 // 0 新用户-有券，1 新用户-无券，2 老用户
) {
    showLoading(true)
    apiSpiceMgr.executeToastKt(ProductCategoryRequest(), success = {
        if (it.isSuccess) {
            it.data?.let { productCategory ->
                showConsultDirectionDialog(couponNum, userStatus, productCategory.img.orEmpty(), arrayListOf<ProductCategory.ProductCategoryBean>().apply {
                    addAll(productCategory.list.orEmpty())
                })
            }
        }
    }, finish = {
        showLoading(false)
    })
}

/**
 * @param couponNum 使用优惠券 但是不指定使用那一张则传-1, 具体使用那一张则传具体优惠券的编号. 0 表示不使用优惠券
 */
private fun FragmentActivity.showConsultDirectionDialog(couponNum: Long, userStatus: Int, topBg: String, productList: MutableList<ProductCategory.ProductCategoryBean>) {
    val dialog = NormalDialogLauncher.newInstance()
    val contentView = layoutInflaterKt(R.layout.dialog_consult_direction)

    val userType = when (userStatus) {
        0 -> "新用户_有券"
        1 -> "新用户_无券"
        else -> "老用户"
    }

    val adapter = ProductAdapter(productList).apply {
        setOnItemClickListener { _, _, position ->
            selectedPos = position
            notifyDataSetChanged()
        }
    }

    contentView.apply {
        findViewById<TextView>(R.id.consult_direction_title).paint.isFakeBoldText = true
        findViewById<TextView>(R.id.consult_direction_confirm).paint.isFakeBoldText = true
        findViewById<RecyclerView>(R.id.consult_direction_rv).apply {
            addItemDecoration(QuickConsultItemDecoration())
            this.adapter = adapter
        }
        findViewById<View>(R.id.consult_direction_close).setOnClickListener {
            dialog.dismissAllowingStateLoss()
        }
        findViewById<ImageView>(R.id.consult_dialog_top_bg).apply {
            glideDisplay(topBg)
        }
        findViewById<View>(R.id.consult_direction_confirm).setOnClickListener {
            if (adapter.selectedPos == -1) {
                UIToast.toast("请选择咨询方向")
            } else {
                // 更改快速咨询发起连线流程，这里不做操作，跳转到匹配的知心达人，然后再发起连线
                val selectedBean = productList[adapter.selectedPos]
                (this@showConsultDirectionDialog as? MeiCustomActivity)?.showMatchingDialog(couponNum, selectedBean.pro_cate_id, userType)
                dialog.dismiss()
                GrowingUtil.track("function_click",
                        "function_name", "快速咨询",
                        "function_page", "选择咨询方向页",
                        "status", userType,
                        "click_type", "${selectedBean.pro_cate_name}品类_确定")
            }
        }
    }

    dialog.showDialog(this, DialogData(customView = contentView, canceledOnTouchOutside = false, backCanceled = false), okBack = null)

    GrowingUtil.track("function_view",
            "function_name", "快速咨询",
            "function_page", "选择咨询方向页",
            "status", userType)
}