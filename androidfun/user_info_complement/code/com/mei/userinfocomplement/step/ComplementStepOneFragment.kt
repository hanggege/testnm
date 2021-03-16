package com.mei.userinfocomplement.step

import android.view.ViewGroup
import android.widget.TextView
import com.mei.wood.R
import kotlinx.android.synthetic.main.fragment_complement_step_one.*

/**
 *  Created by zzw on 2019-11-27
 *  Des:
 */

class ComplementStepOneFragment : ComplementBaseFragment() {

    override val step = 1
    override val title = "请选择你的性别"
    override val layoutId = R.layout.fragment_complement_step_one
    override fun titleView(): TextView {
        return complement_step_one_title
    }

    var genderBack: (Int) -> Unit = {}

    override fun initView() {
        sel_man_ll.isSelected = false
        sel_girl_ll.isSelected = false
    }

    override fun initListener() {
        sel_man_ll.setOnClickListener {
            sel_man_ll.isSelected = true
            sel_girl_ll.isSelected = false
            genderBack(1)
        }

        sel_girl_ll.setOnClickListener {
            sel_man_ll.isSelected = false
            sel_girl_ll.isSelected = true
            genderBack(0)
        }
    }

    override fun provideOverView(): ViewGroup? {
        return view?.parent as? ViewGroup
    }
}