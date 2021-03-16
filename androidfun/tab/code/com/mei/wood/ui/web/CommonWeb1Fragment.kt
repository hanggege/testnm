package com.mei.wood.ui.web

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updatePadding
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.getStatusBarHeight
import com.net.model.chick.tab.TabItem
import launcher.Boom


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/1
 */

class CommonWeb1Fragment : BaseWebFragment() {
    @Boom
    var tabInfo: TabItem? = null

    override var item: TabItem?
        get() = tabInfo
        set(_) {}

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        ActivityLauncher.bind(this)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.apply { updatePadding(top = getStatusBarHeight()) }
    }
}