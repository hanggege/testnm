package com.mei.wood

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.mei.base.network.netchange.NetWorkListener
import com.mei.base.network.netchange.networkController
import com.mei.wood.location.AddressView
import com.mei.wood.location.bindAddressView

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/10/18
 */
class AppFragmentLifeManager : FragmentManager.FragmentLifecycleCallbacks() {

    override fun onFragmentViewCreated(fm: FragmentManager, fragment: Fragment, v: View, savedInstanceState: Bundle?) {
        super.onFragmentViewCreated(fm, fragment, v, savedInstanceState)
        Log.e("currFragment", "ViewCreated: ${fragment::class.java.name}")
        if (fragment is NetWorkListener) networkController().bindView(fragment)


    }

    override fun onFragmentStarted(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentStarted(fm, fragment)

        if (fragment is AddressView) {
            fragment.context?.bindAddressView(fragment)
        }
    }

    override fun onFragmentStopped(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentStopped(fm, fragment)
    }

    override fun onFragmentViewDestroyed(fm: FragmentManager, fragment: Fragment) {
        super.onFragmentViewDestroyed(fm, fragment)
        Log.e("currFragment", "ViewDestroyed: ${fragment::class.java.name}")
        if (fragment is NetWorkListener) networkController().unBindView(fragment)

    }
}