package com.mei.userinfocomplement.step

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.mei.wood.ui.CustomSupportFragment
import com.net.model.chick.tab.tabbarConfig

/**
 * ComplementBaseFragment
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-13
 */
abstract class ComplementBaseFragment : CustomSupportFragment() {

    var complementStep = if (tabbarConfig.setNameRequired) 4 else 3

    abstract val step: Int
    abstract val title: String
    abstract val layoutId: Int
    abstract fun titleView(): TextView

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        titleView().apply {
            paint.isFakeBoldText = true
            text = "$step/$complementStep  $title"
        }
        initView()
        initListener()
        requestData()
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(layoutId, container, false)
    }

    open fun initView() {}
    open fun initListener() {}
    open fun requestData() {}
}