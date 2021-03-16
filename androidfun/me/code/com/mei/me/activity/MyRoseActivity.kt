package com.mei.me.activity

import android.graphics.Color
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.core.view.isGone
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import com.joker.PayFlags
import com.joker.PayManager
import com.joker.PayType
import com.joker.connect.PayCallBack
import com.joker.im.custom.chick.InviteJoinInfo
import com.joker.model.PayFailure
import com.joker.model.PaySuccess
import com.joker.model.order.Order
import com.mei.GrowingUtil
import com.mei.base.ui.nav.Nav
import com.mei.dialog.PayFromType
import com.mei.dialog.myRose
import com.mei.init.spiceHolder
import com.mei.me.adapter.FreeTaskListAdapter
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.transparentStatusBar
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.ext.AmanLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.rx.MeiUiFrame
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.pay.ProductListResult
import com.net.network.chick.pay.OrderCreateRequest
import com.net.network.chick.pay.PayFinishRequest
import com.net.network.chick.pay.ProductListRequest
import kotlinx.android.synthetic.main.activity_my_rose.*

/**
 * author : Song Jian
 * date   : 2020/2/18
 * desc   : 我的心币 充值页面
 */
class MyRoseActivity : MeiCustomActivity(), ISystemInviteJoinShow {
    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_rose)

        transparentStatusBar()
        lightMode()
        title_layout.updatePadding(top = getStatusBarHeight())
        toolbar_bg.updateLayoutParams<RelativeLayout.LayoutParams> {
            height += getStatusBarHeight()
        }

        initView()
        initListener()
        initData()
    }

    private fun initData() {
        requestData()
    }

    private fun requestData() {
        apiSpiceMgr.executeKt(ProductListRequest().apply { showAll = 1;showTask = 1 }, success = {
            if (it.isSuccess && it.data != null) {
                rose_count.text = (it.data.userCoin?.coinBalance ?: 0).toString()
                gift_top_up_view.setData(it.data)

                updateFreeTask(it.data)
            } else {
                UIToast.toast(it.msg)
            }
        }, failure = {
            UIToast.toast(it?.currMessage ?: "请求失败")
        }, finish = {
            (this as? MeiUiFrame)?.showLoading(false)
        })


    }

    private val mFreeTaskList by lazy {
        arrayListOf<ProductListResult.NewbieTaskBean>()

    }

    private val mAdapter by lazy {
        FreeTaskListAdapter(mFreeTaskList)

    }

    private fun initView() {
        free_get_recycler_view.adapter = mAdapter
        rose_count.paint.isFakeBoldText = true

        //知心达人隐藏任务列表
        free_task_layout.isGone = true

    }

    private fun updateFreeTask(data: ProductListResult?) {
        data?.newbieTask?.let {
            mAdapter.setList(it)
        }

    }

    private fun initListener() {
        back_choice.setOnClickListener { finish() }
        consume_record.setOnClickListener {
            Nav.toAmanLink(this, AmanLink.NetUrl.consume_record)
        }
        wechat_pay.setOnClickListener {
            //微信支付
            gift_top_up_view.getSelectedGiftInfo()?.let {
                pay(it.productId, it.depositCoin, PayType.weixin)
            }
        }
        ali_pay.setOnClickListener {
            //支付宝支付
            gift_top_up_view.getSelectedGiftInfo()?.let {
                pay(it.productId, it.depositCoin, PayType.alipay)
            }
        }

        scroll_view.setOnScrollChangeListener { _: NestedScrollView?, _: Int, scrollY: Int, _: Int, _: Int ->
            //offsetTop 向上滑动是负值
            var percent = scrollY / dip(50).toFloat()
            if (percent < 0) percent = 0.0f
            if (percent > 1) percent = 1.0f
            if (percent < 0.2f) {
                //顶部 颜色正常白色 背景隐藏
                back_choice.setImageResource(R.drawable.bg_white_back_arrow)
                consume_record.setTextColor(Color.WHITE)
                rose_title.setTextColor(Color.WHITE)
                toolbar_bg.alpha = 0.0f
            } else {
                //往下拉
                //返回按钮和菜单变黑
                back_choice.setImageResource(R.drawable.bg_black_back_arrow)
                consume_record.setTextColor(Color.BLACK)
                rose_title.setTextColor(Color.BLACK)
                toolbar_bg.alpha = percent
            }
        }
    }

    private fun pay(productId: String, depositRose: Int, @PayFlags payType: Int) {
        getOrder(productId, payType) { order ->
            order?.apply {
                PayManager.performPay(activity, payType, this, object : PayCallBack {
                    override fun onSuccess(success: PaySuccess) {
                        myRose {
                            if (it > 0) {
                                rose_count.text = it.toInt().toString()
                                val dialog = NormalDialogLauncher.newInstance()
                                dialog.showDialog(this@MyRoseActivity, DialogData(message = "恭喜您,您已成功充值${depositRose}心币,当前心币总数: ${it.toInt()}",
                                        cancelStr = "", title = "充值成功"), okBack = {
                                    requestData()
                                    dialog.dismissAllowingStateLoss()
                                })
                            }
                        }
                        success.orderSn?.let {
                            spiceHolder().apiSpiceMgr.executeKt(PayFinishRequest(it))
                        }
                        /**付费成功就上报一次*/
                        GrowingUtil.track("pm_successful", "amount", depositRose.toString())
                    }

                    override fun onFailure(failure: PayFailure?) {
                        UIToast.toast(failure?.err_msg ?: "支付失败")
                    }
                })
            }
        }
    }


    private fun getOrder(productId: String, @PayFlags payType: Int, back: (Order?) -> Unit = { }) {
        apiSpiceMgr.executeToastKt(OrderCreateRequest(productId, PayFromType.MY_ROSE, payType, ""), success = { response ->
            if (response.isSuccess && response.data != null) {
                back(response.data.getOrder(payType))
            } else {
                back(null)
            }
        }, failure = {
            back(null)
        })
    }

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean = false
}