package com.mei.wood.ui

import android.os.Bundle
import com.joker.support.listener.TdHandlerListener
import com.mei.wood.R
import com.mei.wood.rx.MeiUiFrame

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/25
 */
abstract class MeiCustomActivity : MeiCustomBarActivity(), TdHandlerListener, MeiUiFrame {

    override fun showActionBar(): Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.NoActionBar)
        super.onCreate(savedInstanceState)
    }

}