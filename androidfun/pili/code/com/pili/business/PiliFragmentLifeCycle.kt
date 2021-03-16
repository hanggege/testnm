package com.pili.business

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.live.player.presenter.ListenView
import com.live.stream.presenter.StreamView
import com.pili.player.bindPlayerService
import com.pili.stream.bindStreamService

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/9/29
 * Fragment生命周期自动监听音频回调
 */
class PiliFragmentLifeCycle : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentViewCreated(fm: FragmentManager, fragment: Fragment, v: View, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, fragment, v, savedInstanceState)

        if (fragment is ListenView) {
            bindPlayerService().bindView(fragment)
        }
        if (fragment is StreamView) {
            bindStreamService().bindView(fragment)
        }
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentViewDestroyed(fm, fragment)

        if (fragment is ListenView) {
            bindPlayerService().unBindView(fragment)
        }
        if (fragment is StreamView) {
            bindStreamService().unBindView(fragment)
        }
    }
}