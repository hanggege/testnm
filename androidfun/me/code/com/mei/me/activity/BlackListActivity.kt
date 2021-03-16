package com.mei.me.activity

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import com.mei.me.adapter.BlackListAdapter
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenWidth
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.model.chick.friends.BlackListResult
import com.net.network.chick.friends.BlackListRequest
import kotlinx.android.synthetic.main.activity_black_list.*

/**
 * author : Song Jian
 * date   : 2020/1/7
 * desc   : 黑名单列表
 */
class BlackListActivity : MeiCustomActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_black_list)

        initView()
        initListener()
        showLoadingCover()
        requestData()
    }


    private val mFriendList by lazy {
        arrayListOf<BlackListResult>()

    }

    private val mAdapter by lazy {
        BlackListAdapter(mFriendList).apply {
            setEmptyView(LinearLayout(this@BlackListActivity).apply {
                setBackgroundColor(Color.WHITE)
                orientation = LinearLayout.VERTICAL
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
                    gravity = Gravity.CENTER
                }

                addView(ImageView(this@BlackListActivity).apply {
                    setImageResource(R.drawable.empty_default_icon)
                    val w = screenWidth
                    val h = (w * 582 * 1f / 1500).toInt()
                    layoutParams = ViewGroup.LayoutParams(w, h)
                })
                addView(TextView(this@BlackListActivity).apply {
                    layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                        setPadding(dip(20))
                    }
                    text = "暂无更多数据"
                    setTextColor(Color.parseColor("#989898"))
                    textSize = 15.0f
                })
            })
        }

    }

    private fun initView() {
        black_list_recy.adapter = mAdapter


    }

    private fun initListener() {
        black_list_back.setOnClickListener { finish() }
    }

    private fun requestData() {
        apiSpiceMgr.executeKt(BlackListRequest(), {
            if (it.isSuccess) {
                it.data?.let { data ->
                    mAdapter.setList(data)
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