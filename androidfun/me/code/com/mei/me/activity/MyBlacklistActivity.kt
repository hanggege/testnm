package com.mei.me.activity

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.Cxt
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.report.ShieldListInfo
import com.net.network.chick.report.BlackListRequest
import com.net.network.chick.report.ShieldingDeleteRequest
import kotlinx.android.synthetic.main.activity_my_black_list.*

/**
 *
 * @author Created by lenna on 2020/10/23
 * 黑名单列表
 */
class MyBlacklistActivity : MeiCustomBarActivity() {
    private val blackList = arrayListOf<ShieldListInfo.ListBean>()
    private val listAdapter by lazy {
        BlackListAdapter(blackList).apply {
            loadMoreModule.setOnLoadMoreListener {
                //加载更多
                loadMoreBlackList()
            }
            addChildClickViewIds(R.id.cancel_black_list)
            setOnItemChildClickListener { _, _, position ->
                showLoading(true)
                apiSpiceMgr.executeKt(ShieldingDeleteRequest(blackList[position].targetId.toString(), ""), success = {
                    if (it.isSuccess) {
                        val black = blackList[position]
                        postAction(CANCEL_SHIELD_USER, if (black.targetInfo?.groupId != 0 && it.data?.userIds?.isNotEmpty() == true) it.data?.userIds else arrayListOf(blackList[position].targetId))
                        blackList.removeAt(position)
                        notifyItemRemoved(position)
                        UIToast.toast(it.msg)
                        black_list_empty_page.setEmptyText("暂无黑名单")
                        black_list_empty_page.setBtnVisible(false)
                        black_list_empty_page.isVisible = blackList.isEmpty()
                    } else {
                        UIToast.toast(it.errMsg)
                    }
                }, failure = {
                    UIToast.toast(it?.currMessage)
                }, finish = {
                    showLoading(false)
                })
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_black_list)
        title = "黑名单"
        initView()
        getBlackList()
    }

    private fun initView() {
        blacklist_rv.layoutManager = LinearLayoutManager(this)
        blacklist_rv.adapter = listAdapter
        black_list_empty_page.setBtnClickListener(View.OnClickListener { getBlackList() })
    }

    /**
     * 获取黑名单列表
     */
    private fun getBlackList() {
        showLoading(true)
        apiSpiceMgr.executeToastKt(BlackListRequest(""), success = { response ->
            if (response.isSuccess && response.data != null) {
                black_list_empty_page.isVisible = false
                blackList.clear()
                blackList.addAll(response.data.list)

                listAdapter.loadMoreModule.isEnableLoadMore = blackList.size > 5

                if (response.data.hasMore) {
                    listAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    listAdapter.loadMoreModule.loadMoreEnd()
                }
                listAdapter.notifyDataSetChanged()
                if (blackList.isEmpty()) {
                    black_list_empty_page.setEmptyText("暂无黑名单")
                    black_list_empty_page.setBtnVisible(false)
                    black_list_empty_page.isVisible = blackList.isEmpty()
                } else {
                    black_list_empty_page.setEmptyText(Cxt.getStr(R.string.no_network))
                    black_list_empty_page.setBtnVisible(true)
                }

            } else {
                black_list_empty_page.isVisible = true
            }
        }, failure = {
            black_list_empty_page.isVisible = true
            UIToast.toast(it?.currMessage)
        }, finish = {
            showLoading(false)
        })
    }

    /**
     * 加载更多黑名单数据
     */
    private fun loadMoreBlackList() {
        showLoading(true)
        apiSpiceMgr.executeToastKt(BlackListRequest(""), success = { response ->
            if (response.isSuccess && response.data != null && response.data.list.isNotEmpty()) {
                blackList.addAll(blackList.size, response.data.list)
                if (response.data.hasMore) {
                    listAdapter.loadMoreModule.loadMoreComplete()
                } else {
                    listAdapter.loadMoreModule.loadMoreEnd()
                }
                listAdapter.notifyItemRangeInserted(blackList.size - response.data.list.size, response.data.list.size)
            }
        }, failure = {
            UIToast.toast(it?.currMessage)
        }, finish = {
            showLoading(false)
        })
    }
}

/**
 * 黑名单列表适配
 */
class BlackListAdapter(listBean: MutableList<ShieldListInfo.ListBean>)
    : BaseQuickAdapter<ShieldListInfo.ListBean, BaseViewHolder>(R.layout.item_user_black_list, listBean), LoadMoreModule {

    override fun convert(holder: BaseViewHolder, item: ShieldListInfo.ListBean) {
        holder.getView<RoundImageView>(R.id.user_avatar_riv)
                .glideDisplay(item.targetInfo?.avatar.orEmpty(), item.targetInfo?.gender.genderAvatar(item.targetInfo?.isPublisher))
        holder.getView<TextView>(R.id.user_name_tv).apply {
            text = item.targetInfo?.nickname.orEmpty()
            paint.isFakeBoldText = true
        }

        val timeTxt = "拉黑时间"
        holder.setText(R.id.tv_black_list_set_time, "${timeTxt}：${item.createTime}")
        holder.getView<TextView>(R.id.cancel_black_list).apply {
            text = "解除"
        }
        holder.getView<TextView>(R.id.level_tv).apply {
            setPadding(0, 0, if (item.targetInfo?.userLevel ?: 0 > 9) dip(5) else dip(9), 0)
            isVisible = item.targetInfo?.userLevel ?: 0 > 0
            text = item.targetInfo?.userLevel.toString()
            setBackgroundResource(getBackGroundLevelResource(item.targetInfo?.userLevel
                    ?: 0, LevelSize.Normal))
        }
    }

}