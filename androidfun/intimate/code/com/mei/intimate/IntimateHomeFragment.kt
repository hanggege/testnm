package com.mei.intimate

import android.os.Bundle
import android.util.SparseIntArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.isEmpty
import androidx.core.util.isNotEmpty
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mei.GrowingUtil
import com.mei.base.common.*
import com.mei.intimate.adapter.IntimateHomeAdapter
import com.mei.intimate.adapter.IntimateHomeDecoration
import com.mei.intimate.adapter.setItemLocation
import com.mei.intimate.view.CustomHeader
import com.mei.live.action.showTestReportDialog
import com.mei.orc.event.bindAction
import com.mei.orc.event.postAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getStatusBarHeight
import com.mei.orc.tab.TabItemContent
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.LoadType
import com.net.MeiUser
import com.net.model.chick.room.RoomItemType
import com.net.model.chick.room.RoomList
import com.net.model.chick.room.RoomList.BaseItem
import com.net.network.chick.tab.HomepageIndexRequest
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.fragment_intimate.*
import java.util.concurrent.TimeUnit

/**
 * Created by hang on 2020-02-13.
 */
class IntimateHomeFragment : TabItemFragment(), TabItemContent {

    private var pageNo = 1
    private val mList = arrayListOf<BaseItem<*>>()

    private var webVersion = SparseIntArray()

    private val mIntimateAdapter by lazy {
        IntimateHomeAdapter(mList).apply {
            setHasStableIds(true)
        }
    }

    private val classicsHeader by lazy {
        CustomHeader(context)
    }

    private val itemDecoration = IntimateHomeDecoration()
    private val mScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            val firstPosition = gridLayoutManager.findFirstVisibleItemPosition()
            val lastPosition = gridLayoutManager.findLastVisibleItemPosition()
            //是否顶部
            val isTop = firstPosition == 0
            //是否底部
            val isBottom = lastPosition == gridLayoutManager.itemCount.minus(1)

            when (newState) {
                RecyclerView.SCROLL_STATE_IDLE -> {
                    if (!isTop && !isBottom) {
                        postAction(RECYCLE_SCROLL_STATE, true)
                    }
                }
                else -> {
                    if (!isTop && !isBottom) {
                        postAction(RECYCLE_SCROLL_STATE, false)
                    }
                }
            }
        }
    }

    private val gridLayoutManager by lazy { GridLayoutManager(activity, 2) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
            inflater.inflate(R.layout.fragment_intimate, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        webVersion.clear()
        requestData(LoadType.First)
        bindAction<Boolean>(CHANG_LOGIN) {
            pageNo = 1
            webVersion.clear()
            requestData(LoadType.First)
        }
        bindAction<Boolean>(REFRESH_HOME_DATA) {
            pageNo = 1
            webVersion.clear()
            requestData(LoadType.First)
        }
        bindAction<Pair<Int, Int>>(RESET_WEB_VERSION) {
            it?.let {
                webVersion.put(it.first, it.second)
            }
        }
        bindAction<List<Int>>(SHIELD_USER) {
            pageNo = 1
            requestData(LoadType.pull_refresh)
        }
        bindAction<List<Int>>(CANCEL_SHIELD_USER) {
            pageNo = 1
            requestData(LoadType.pull_refresh)
        }
        bindAction<Boolean>(NEW_PEOPLE_GIFT_BAG_PAY_SUCCESS) {
            pageNo = 1
            requestData(LoadType.pull_refresh)
        }

        Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindToLifecycle())
                .subscribeBy {
                    val firstPosition = gridLayoutManager.findFirstVisibleItemPosition()
                    val lastPosition = gridLayoutManager.findLastVisibleItemPosition()

                    exclusiveDurations.clear()
                    firstExclusiveIndex = 0

                    if (lastPosition > firstPosition) {
                        (firstPosition..lastPosition).forEach {
                            val baseItem = mList.getOrNull(it)
                            if (baseItem != null
                                    && (baseItem.type == RoomItemType.SPECIAL_BROADCAST || baseItem.type == RoomItemType.EXCLUSIVE_BROADCAST)) {
                                (baseItem.content as? RoomList.BroadcastItemBean)?.run {
                                    if (exclusiveDurations.isEmpty()) {
                                        firstExclusiveIndex = it
                                    }
                                    duration++
                                    exclusiveDurations.put(it, duration)
                                }
                            }
                        }

                        if (exclusiveDurations.isNotEmpty() && firstExclusiveIndex > 0) {
                            /**当前专属服务可见在列表中,刷新连麦时长*/
                            if (maxOf(firstPosition, firstExclusiveIndex) <= minOf(lastPosition, firstExclusiveIndex + exclusiveDurations.size() - 1)) {
                                val firstNotifyPosition = maxOf(firstPosition, firstExclusiveIndex)
                                val notifyCount = minOf(lastPosition, firstExclusiveIndex + exclusiveDurations.size() - 1) - firstNotifyPosition + 1
                                mIntimateAdapter.notifyItemRangeChanged(firstNotifyPosition, notifyCount, exclusiveDurations)
                            }
                        }
                    }
                }
    }

    private var exclusiveDurations = SparseIntArray()
    private var firstExclusiveIndex = 0

    private fun resetWebVersion(list: List<BaseItem<*>>) {
        webVersion.clear()
        list.forEach {
            if (it.type == RoomItemType.BANNER_WITH_URL || it.type == RoomItemType.BANNER_WITH_HTML || it.type == RoomItemType.NAVIGATE_BAR) {
                val content = it.content as? RoomList.HomePageNavigateBar
                if (content != null) {
                    webVersion.put(content.seqId, content.version)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun requestData(type: LoadType) {
        if (type == LoadType.First) {
            showLoading(true)
        }
        apiSpiceMgr.executeToastKt(HomepageIndexRequest(pageNo, webVersion.toString()), success = {
            val data = it.data
            if (it.isSuccess && data != null) {
                // 有测试报告内容
                data.report?.let { report -> (context as? TouristsActivity)?.showTestReportDialog(report) }
                // 下拉slogan文案刷新
                data.topSlogan?.let { slogan ->
                    if (slogan != classicsHeader.headerText) {
                        classicsHeader.headerText = slogan
                    }
                }
                val list = data.list
                if (pageNo == 1) {
                    mList.clear()

                    resetWebVersion(list)
                }

                mList.addAll(list)
                setItemLocation(mList)
                mIntimateAdapter.loadMoreModule.isEnableLoadMore = mList.size > 7

                if (mList.size == list.size) {
                    mIntimateAdapter.notifyDataSetChanged()
                } else {
                    mIntimateAdapter.notifyItemRangeInserted(mList.size - list.size, list.size)
                }
                if (data.hasMore) {
                    mIntimateAdapter.loadMoreModule.loadMoreComplete()
                    //加载成功后如果还有下一页
                    pageNo = data.nextPageNo
                } else {
                    mIntimateAdapter.loadMoreModule.loadMoreEnd(true)
                }
            } else {
                mIntimateAdapter.loadMoreModule.loadMoreFail()
            }
            empty_layout.setEmptyImage(R.drawable.home_empty_page)
                    .setEmptyText(if (it.isSuccess) "知心达人休息中，去发现页看看吧" else "网络开小差了")
                    .setBtnVisible(!it.isSuccess)
                    .setTopMargin(dip(123))
                    .setBtnClickListener(View.OnClickListener {
                        pageNo = 1
                        requestData(LoadType.First)
                    })
            empty_layout.isVisible = mList.isEmpty()
        }, failure = {
            empty_layout.setEmptyImage(R.drawable.home_error_page)
                    .setEmptyText("网络开小差了")
                    .setBtnVisible(true)
                    .setTopMargin(dip(123))
                    .setBtnClickListener(View.OnClickListener {
                        pageNo = 1
                        requestData(LoadType.First)
                    })
            if (pageNo > 1) {
                mIntimateAdapter.loadMoreModule.loadMoreFail()
            }
            empty_layout.isVisible = mList.isEmpty()
        }, finish = {
            showLoading(false)
            intimate_refresh.finishRefresh()
        })
    }

    private fun initView() {
        intimate_refresh.setOnRefreshListener {
            pageNo = 1
            requestData(LoadType.pull_refresh)
        }
        intimate_recycler.apply {
            layoutManager = gridLayoutManager
            adapter = mIntimateAdapter
            addOnScrollListener(mScrollListener)
            addItemDecoration(itemDecoration)
        }
        mIntimateAdapter.loadMoreModule.setOnLoadMoreListener {
            requestData(LoadType.load_more)
        }
        intimate_refresh.setRefreshHeader(classicsHeader)
        intimate_refresh.updateLayoutParams<ViewGroup.MarginLayoutParams> {
            topMargin = getStatusBarHeight()
        }
    }

    override fun willAppear() {
        super.willAppear()
        GrowingUtil.track("main_page_view",
                "screen_name", "首页",
                "main_app_gn_pro", MeiUser.getSharedUser().info?.interests.orEmpty().joinToString { it.name },
                "main_app_gn_type", "直播、短视频",
                "time_stamp", (System.currentTimeMillis() / com.mei.orc.unit.TimeUnit.SECOND).toString())
    }

    override fun onSelect() {

    }

    override fun onDeSelect() {

    }

    override fun onReSelect() {
        if (isAdded) {
            val firstPosition = gridLayoutManager.findFirstVisibleItemPosition()
            if (firstPosition > 0) {
                intimate_recycler.scrollToPosition(0)
                pageNo = 1
                requestData(LoadType.First)
            }
        }
    }


}