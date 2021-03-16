package com.mei.me.adapter

import android.widget.TextView
import androidx.core.view.isVisible
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.common.SELECT_HOME
import com.mei.base.ui.nav.Nav
import com.mei.me.activity.PersonalInformationActivityLauncher
import com.mei.orc.event.postAction
import com.mei.wood.R
import com.net.model.chick.pay.ProductListResult

/**
 * author : Song Jian
 * date   : 2020/1/7
 * desc   : 充值页面免费获取心币任务列表
 */
class FreeTaskListAdapter(val list: ArrayList<ProductListResult.NewbieTaskBean>) :
        BaseQuickAdapter<ProductListResult.NewbieTaskBean, BaseViewHolder>(R.layout.item_free_task_list, list) {


    override fun convert(holder: BaseViewHolder, item: ProductListResult.NewbieTaskBean) {
        //任务内容
        holder.getView<TextView>(R.id.free_task_list_content).apply {
            text = "${item.taskText}: ${item.coinText}心币"
        }
        //未完成
        holder.getView<TextView>(R.id.free_task_list_ready).apply {
            isVisible = !item.isFinish
            setOnClickListener {
                if ("PERFECT_BIND" == item.taskType) {
                    //进入个人资料页
                    PersonalInformationActivityLauncher.startActivity(context)
                } else {
                    //进入首页
                    postAction(SELECT_HOME, true)
                    Nav.toMain(context)
                }


            }
        }
        //已完成
        holder.getView<TextView>(R.id.free_task_list_done).apply {
            isVisible = item.isFinish
        }

    }

}