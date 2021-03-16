package com.mei.me.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.mei.base.ui.nav.Nav
import com.mei.me.adapter.MyFollowListAdapter
import com.mei.orc.Cxt
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.chick.friends.SubListBean
import com.net.network.chick.friends.MyFollowkListRequest
import kotlinx.android.synthetic.main.activity_my_follow_list.*
import kotlinx.android.synthetic.main.include_net_error_layout.*

/**
 * author : Song Jian
 * date   : 2020/1/18
 * desc   : 我的关注列表
 */
class MyFollowListActivity : MeiCustomBarActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_follow_list)

        title = "我的关注"
        initView()
        initListener()
        showLoadingCover()
        requestData()

    }

    private var hasMore: Boolean = false
    private var pageNo = 1
    private val mFollowList: ArrayList<SubListBean> = arrayListOf()

    private val mAdapter by lazy {
        MyFollowListAdapter(mFollowList).apply {
            setEmptyView(LinearLayout(this@MyFollowListActivity).apply {
                setBackgroundColor(Color.WHITE)
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    gravity = Gravity.CENTER
                }

                addView(ImageView(this@MyFollowListActivity).apply {
                    setImageResource(R.drawable.empty_my_follow_icon)
                    val w = screenWidth
                    val h = (w * 582 * 1f / 1500).toInt()
                    layoutParams = ViewGroup.LayoutParams(w, h)
                })
                addView(TextView(this@MyFollowListActivity).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        setPadding(dip(20))
                    }
                    text = "暂无关注"
                    setTextColor(Color.parseColor("#989898"))
                    textSize = 15.0f
                })
            })
            setOnItemClickListener { adapter, _, position ->
                (adapter.getItemOrNull(position) as? SubListBean)?.let {
                    Nav.toPersonalPage(activity, it.userId)
                }
            }
        }

    }

    private fun initView() {
        my_follow_list_recy.adapter = mAdapter
    }

    private fun initListener() {
        net_error_layout.setEmptyText(Cxt.getStr(R.string.empty_here))
                .setOnBtnClick(View.OnClickListener {
                    pageNo = 1
                    requestData()
                })

        mAdapter.loadMoreModule.setOnLoadMoreListener {
            if (hasMore) {
                requestData()
            }
        }
    }

    override fun onRestart() {
        super.onRestart()
        pageNo = 1
        requestData()
    }


    private fun requestData() {
        apiSpiceMgr.executeKt(MyFollowkListRequest(pageNo), {
            if (it.isSuccess) {
                it.data?.list?.let { data ->
                    if (pageNo == 1) {
                        mFollowList.clear()
                    }
                    mAdapter.addData(data.list)

                    //如果列表一屏幕都没铺满并且就没有更多数据了，那么没有更多了展示很丑。影藏展示
                    mAdapter.loadMoreModule.isEnableLoadMore = mFollowList.size > 5
                    hasMore = data.hasMore
                    if (data.hasMore) {
                        mAdapter.loadMoreModule.loadMoreComplete()
                        //加载成功后如果还有下一页
                        pageNo = data.nextPageNo
                    } else {
                        mAdapter.loadMoreModule.loadMoreEnd()
                    }
                }
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