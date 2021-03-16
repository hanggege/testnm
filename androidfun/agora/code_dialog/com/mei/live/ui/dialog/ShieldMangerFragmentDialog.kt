package com.mei.live.ui.dialog

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.core.text.buildSpannedString
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mei.base.common.CANCEL_SHIELD_USER
import com.mei.base.network.holder.SpiceHolder
import com.mei.init.spiceHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.widget.empty.EmptyPageView
import com.mei.widget.round.RoundImageView
import com.mei.wood.R
import com.mei.wood.extensions.appendLink
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.net.model.report.ShieldListInfo
import com.net.network.chick.report.BlackListRequest
import com.net.network.chick.report.KeeperDeleteRequest
import com.net.network.chick.report.KeeperListRequest
import com.net.network.chick.report.ShieldingDeleteRequest
import kotlinx.android.synthetic.main.shield_manager_dialog.cancel
import kotlinx.android.synthetic.main.shield_manager_fragment_dialog.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.dialog
 * @ClassName:      ShieldMangerDialog
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/16 14:07
 * @UpdateUser:
 * @UpdateDate:     2020/6/16 14:07
 * @UpdateRemark:
 * @Version:
 */
fun FragmentActivity.showShieldMangerFragmentDialog(roomId: String, type: Int) {
    ShieldMangerFragmentDialog().apply {
        this.roomId = roomId
        this.type = type
    }.show(supportFragmentManager, "ShieldMangerDialog")
}

@Suppress("TYPE_INFERENCE_ONLY_INPUT_TYPES_WARNING")
class ShieldMangerFragmentDialog : BottomDialogFragment() {
    var roomId = ""

    //0 是拉黑列表 1是管理员名单
    var type = 0
    var nextPageNo = 1
    private var hashMore = false
    private var mShields: MutableList<ShieldListInfo.ListBean> = arrayListOf()
    private val mShieldedListAdapter by lazy {
        ShieldedListAdapter(type, mShields)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.shield_manager_fragment_dialog, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }

        mShieldedListAdapter.addChildClickViewIds(R.id.cancle_shield)
        mShieldedListAdapter.setOnItemChildClickListener { _, _, position ->
            when (type) {
                0 -> {
                    shieldedDelete(position)
                }
                1 -> {
                    keeperDelete(position)
                }
            }

        }
        recycler_shield_manager.layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
        mShieldedListAdapter.loadMoreModule.setOnLoadMoreListener {
            if (hashMore) {
                loadMore()
            }
        }
        recycler_shield_manager.adapter = mShieldedListAdapter
        //数据类型区分
        when (type) {
            0 -> {
                shieldedList()
            }
            1 -> {
                keeperList()
            }
        }

    }

    /**
     * 管理员名单
     */
    private fun keeperList() {
        val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        apiClient.executeToastKt(KeeperListRequest(roomId), success = { it ->
            if (it.isSuccess && it.data != null) {
                val data = it.data
                hashMore = data.hasMore
                nextPageNo = data.nextPageNo
                mShieldedListAdapter.loadMoreModule.isEnableLoadMore = hashMore
                shield_manager_title.apply {
                    text = buildSpannedString {
                        appendLink(if (type == 0) "黑名单" else "助教设置", Color.parseColor("#333333"), 1.2f)
                        if (data.total > 0) {
                            appendLink("(${data.total})", Color.parseColor("#999999"), 1.0f)
                        }
                    }
                }

                it.data?.list?.let {
                    mShields.addAll(it)
                    mShieldedListAdapter.notifyChanged()
                }
            } else {
                dismissAllowingStateLoss()
            }
        }, failure = {
            dismissAllowingStateLoss()
        })
    }

    /**
     * 拉黑名单
     */
    private fun shieldedList() {
        val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        apiClient.executeToastKt(BlackListRequest(roomId), success = { it ->
            if (it.isSuccess && it.data != null) {
                val data = it.data
                hashMore = data.hasMore
                nextPageNo = data.nextPageNo
                mShieldedListAdapter.loadMoreModule.isEnableLoadMore = hashMore
                shield_manager_title.apply {
                    text = buildSpannedString {
                        appendLink(if (type == 0) "黑名单" else "助教设置", Color.parseColor("#333333"), 1.2f)
                        if (data.total > 0) {
                            appendLink("(${data.total})", Color.parseColor("#999999"), 1.0f)
                        }
                    }
                }

                data.list?.let {
                    mShields.addAll(it)
                    mShieldedListAdapter.notifyChanged()
                }
            } else {
                dismissAllowingStateLoss()
            }
        }, failure = {
            dismissAllowingStateLoss()
        })
    }

    /**
     * 加载更多
     */
    private fun loadMore() {
        val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
        when (type) {
            0 -> {
                apiClient.executeToastKt(BlackListRequest(roomId), success = { it ->
                    it.data?.let {
                        hashMore = it.hasMore
                        if (hashMore) {
                            nextPageNo = it.nextPageNo
                            mShieldedListAdapter.loadMoreModule.loadMoreComplete()
                        } else {
                            mShieldedListAdapter.loadMoreModule.loadMoreEnd()
                        }
                        mShields.addAll(it.list.orEmpty())
                        mShieldedListAdapter.notifyChanged()
                    }
                }, failure = {
                    mShieldedListAdapter.loadMoreModule.loadMoreFail()
                })
            }
            1 -> {
                apiClient.executeToastKt(KeeperListRequest(roomId), success = { it ->
                    it.data?.let {
                        hashMore = it.hasMore
                        if (hashMore) {
                            nextPageNo = it.nextPageNo
                            mShieldedListAdapter.loadMoreModule.loadMoreComplete()
                        } else {
                            mShieldedListAdapter.loadMoreModule.loadMoreEnd()
                        }
                        mShields.addAll(it.list.orEmpty())
                        mShieldedListAdapter.notifyChanged()
                    }
                }, failure = {
                    mShieldedListAdapter.loadMoreModule.loadMoreFail()
                })

            }
        }
    }


    /**
     * 取消黑名单
     */
    private fun shieldedDelete(position: Int) {
        mShields.getIndexOrNull(position)?.let { it ->
            val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
            apiClient.executeKt(ShieldingDeleteRequest(it.targetId.toString(), roomId), success = { response ->
                if (response.isSuccess) {
                    postAction(CANCEL_SHIELD_USER, arrayListOf(it.targetId))
                    mShields.removeAt(position)
                    mShieldedListAdapter.notifyChanged()
                    shield_manager_title.text = buildSpannedString {
                        appendLink(if (type == 0) "黑名单" else "助教设置", Color.parseColor("#333333"), 1.2f)
                        if (mShields.size > 0) {
                            appendLink("(${mShields.size})", Color.parseColor("#999999"), 1.0f)
                        }
                    }
                    UIToast.toast(response.msg)
                }
            })
        }
    }

    /**
     * 取消管理员设置
     */
    private fun keeperDelete(position: Int) {
        mShields.getIndexOrNull(position)?.let { it ->
            val apiClient = (activity as? SpiceHolder)?.apiSpiceMgr ?: spiceHolder().apiSpiceMgr
            apiClient.executeKt(KeeperDeleteRequest(roomId, it.userId.toString()), success = {
                if (it.isSuccess) {
                    mShields.removeAt(position)
                    mShieldedListAdapter.notifyItemRemoved(position)
                    shield_manager_title.text = buildSpannedString {
                        appendLink(if (type == 0) "黑名单" else "助教设置", Color.parseColor("#333333"), 1.2f)
                        if (mShields.size > 0) {
                            appendLink("(${mShields.size})", Color.parseColor("#999999"), 1.0f)
                        }
                    }
                    UIToast.toast(it.msg)
                }
            })
        }
    }

    class ShieldedListAdapter(var type: Int, listBean: MutableList<ShieldListInfo.ListBean>) : BaseQuickAdapter<ShieldListInfo.ListBean, BaseViewHolder>(R.layout.item_shield_manager_dialog, listBean), LoadMoreModule {

        override fun convert(holder: BaseViewHolder, item: ShieldListInfo.ListBean) {
            holder.getView<RoundImageView>(R.id.user_avatar).glideDisplay(item.targetInfo?.avatar.orEmpty(), item.targetInfo?.gender.genderAvatar(item.targetInfo?.isPublisher))
            holder.getView<TextView>(R.id.user_name).apply {
                text = item.targetInfo?.nickname.orEmpty()
                paint.isFakeBoldText = true
            }

            val timeTxt = if (type == 0) "拉黑时间" else "设置时间"
            holder.setText(R.id.tv_shield_set_time, "${timeTxt}：${item.createTime}")
            holder.getView<TextView>(R.id.cancle_shield).apply {
                text = if (type == 0) "解除" else "取消"
                isVisible = true
            }
            holder.getView<TextView>(R.id.tv_level).apply {
                setPadding(0, 0, if (item.targetInfo?.userLevel ?: 0 > 9) dip(5) else dip(9), 0)
                isVisible = item.targetInfo?.userLevel ?: 0 > 0
                text = item.targetInfo?.userLevel.toString()
                setBackgroundResource(getBackGroundLevelResource(item.targetInfo?.userLevel
                        ?: 0, LevelSize.Normal))
            }
        }

        fun notifyChanged() {
            if (!hasEmptyView() && data.size <= 0) {
                setEmptyView(EmptyPageView(context).apply {
                    setEmptyText("暂无数据")
                    setEmptyCenter()
                    setVerticalGravity(Gravity.CENTER_VERTICAL)
                    setBackgroundColor(Color.WHITE)
                    setBtnVisible(false)
                }
                )
            }
            notifyDataSetChanged()

        }
    }
}
