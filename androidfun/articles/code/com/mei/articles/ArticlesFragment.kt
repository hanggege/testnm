package com.mei.articles

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import com.mei.articles.adapter.ArticlesAdapter
import com.mei.articles.adapter.ITEM_TYPE_ELITE_1
import com.mei.articles.adapter.ITEM_TYPE_ELITE_2
import com.mei.articles.adapter.ITEM_TYPE_TITLE
import com.mei.base.baseadapter.multi.MultipleItem
import com.mei.orc.ext.getStatusBarHeight
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.fragment.TabItemFragment
import com.net.model.chick.room.HandPickResult
import com.net.network.chick.handpick.HandPickRequest
import kotlinx.android.synthetic.main.fragmen_articles.*
import kotlinx.android.synthetic.main.include_net_error_layout.*

/**
 *  Created by zzw on 2019-12-20
 *  Des:
 */

class ArticlesFragment : TabItemFragment() {
    private val mListData = arrayListOf<MultipleItem<*>>()

    private val mAdapter: ArticlesAdapter by lazy {
        ArticlesAdapter(mListData)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragmen_articles, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articles_top.updatePadding(
                top = articles_top.paddingTop + getStatusBarHeight())

        articles_refresh.setOnRefreshListener {
            request()
        }
        articles_recy.adapter = mAdapter

        showLoadingCover()
        request()
    }

    private fun request() {
        apiSpiceMgr.executeKt(HandPickRequest(),
                success = {
                    it.data?.let { data ->
                        convertData(data)
                        net_error_layout.setEmptyImage(R.drawable.empty_activity_icon)
                                .setEmptyText(getString(R.string.empty_here))
                                .setBtnVisible(false)
                    }
                    net_error_layout.isVisible = mListData.isEmpty()
                    articles_recy.isVisible = mListData.isNotEmpty()
                }, failure = {
            net_error_layout.setEmptyImage(R.drawable.empty_net_error_icon)
                    .setEmptyText(getString(R.string.no_network))
                    .setBtnVisible(true)
                    .setOnBtnClick(View.OnClickListener {
                        showLoading(true)
                        request()
                    })

            net_error_layout.isVisible = mListData.isEmpty()
            articles_recy.isVisible = mListData.isNotEmpty()
        }, finish = {
            articles_refresh.isRefreshing = false
            showLoading(false)
        })
    }


    /**
     * 转换数据
     */
    private fun convertData(data: HandPickResult) {
        mListData.clear()
        val eliteCateOverviewList = data.elite_cate_overview.orEmpty()
        /**其他干货分类**/
        if (eliteCateOverviewList.isNotEmpty()) {
            eliteCateOverviewList.forEachIndexed { index, elite_cate_overview ->
                if (elite_cate_overview.list.orEmpty().isNotEmpty()) {
                    mListData.add(MultipleItem<String>(ITEM_TYPE_TITLE, elite_cate_overview.name))
                    elite_cate_overview.list?.run {
                        forEachIndexed { _, elite_cate_item ->
                            if (index % 2 == 0) {
                                mListData.add(MultipleItem<HandPickResult.Elite_cate_item>(ITEM_TYPE_ELITE_2, elite_cate_item))
                            } else {
                                mListData.add(MultipleItem<HandPickResult.Elite_cate_item>(ITEM_TYPE_ELITE_1, elite_cate_item))
                            }
                        }
                    }
                }
            }
        }
        mAdapter.notifyDataSetChanged()
    }
}