package com.mei.userinfocomplement.step

import android.view.ViewGroup
import android.widget.TextView
import com.mei.userinfocomplement.adapter.RangeOfAgeAdapter
import com.mei.userinfocomplement.adapter.RangeOfAgeItemDecoration
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.network.chick.user.RangeOfAgeRequest
import kotlinx.android.synthetic.main.fragment_complement_step_two.*

/**
 *  Created by zzw on 2019-11-27
 *  Des:
 */

class ComplementStepTwoFragment : ComplementBaseFragment() {

    override val step = 2
    override val title = "请选择你的年龄"
    override val layoutId = R.layout.fragment_complement_step_two
    override fun titleView(): TextView {
        return complement_step_two_title
    }

    var ageBack: (String) -> Unit = {}
    private var years = arrayListOf<String>()
    private val mAdapter by lazy {
        RangeOfAgeAdapter(years).apply {
            setOnItemClickListener { _, _, position ->
                selectedPos = position
                notifyItemRangeChanged(selectedPos, 1)
                fragment_complement_age_rv.postDelayed({ageBack(years[position])},500)
            }
        }
    }

    override fun initView() {
        fragment_complement_age_rv.adapter = mAdapter
        fragment_complement_age_rv.addItemDecoration(RangeOfAgeItemDecoration())
    }

    override fun requestData() {
        apiSpiceMgr.executeKt(RangeOfAgeRequest(), success = {
            if (it.isSuccess && !it.data?.years.isNullOrEmpty()) {
                years.clear()
                years.addAll(it.data.years)
                mAdapter.notifyDataSetChanged()
            }
        })
    }

    override fun provideOverView(): ViewGroup? {
        return view?.parent as? ViewGroup
    }


}