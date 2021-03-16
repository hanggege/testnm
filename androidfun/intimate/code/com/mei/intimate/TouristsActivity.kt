package com.mei.intimate

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.commit
import com.mei.init.exitApp
import com.mei.orc.ext.lightMode
import com.mei.orc.ext.transparentStatusBar
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.isOnDoubleClick
import com.mei.wood.R
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.util.AppManager

/**
 * TouristsActivity
 *
 * 游客模式主页
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-11-13
 */
class TouristsActivity : MeiCustomActivity() {

    private val mainView: View by lazy {
        FrameLayout(this).apply {
            id = R.id.main_content_root
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundColor(Color.WHITE)
        }
    }

    override fun customStatusBar(): Boolean {
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainView)
        transparentStatusBar()
        lightMode()

        supportFragmentManager.commit(allowStateLoss = true) {
            val fragment = IntimateHomeFragment()
            add(R.id.main_content_root, fragment)
            show(fragment)
        }

    }

    override fun onBackPressed() {
        if (isOnDoubleClick(2000, "finish App")) {
            AppManager.getInstance().finishAllActivity()
            exitApp()
        } else {
            UIToast.toast(this, this.getString(R.string.two_back_finish))
        }
    }

}