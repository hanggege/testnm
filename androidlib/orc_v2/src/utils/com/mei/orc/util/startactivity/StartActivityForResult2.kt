@file:Suppress("DEPRECATION")

package com.mei.orc.util.startactivity

import android.app.Activity
import android.app.Fragment
import android.content.Intent
import android.os.Build

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 * 针对startActivityForResult 封装
 */

private const val ResultTag = "StartActivityForResult2"



fun Activity.startFragmentForResult2(intent: Intent, requestCode: Int, back: (requestCode: Int, data: Intent?) -> Unit = { _, _ -> }) {
    fragmentManager?.let { manager ->
        try {
            val fragment = (manager.findFragmentByTag(ResultTag) as? StartActivityForResult2)
                    ?: StartActivityForResult2().apply {
                        val transaction = manager.beginTransaction().add(this, ResultTag)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            transaction.commitNowAllowingStateLoss()
                        } else {
                            transaction.commitAllowingStateLoss()
                            manager.executePendingTransactions()
                        }
                    }
            fragment.startFragmentForResult(intent, requestCode, back)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

class StartActivityForResult2 : Fragment() {

    private var backResult: (requestCode: Int, data: Intent?) -> Unit = { _, _ -> }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            backResult(requestCode, data)
        }
    }

    fun startFragmentForResult(intent: Intent, requestCode: Int, back: (requestCode: Int, data: Intent?) -> Unit = { _, _ -> }) {
        backResult = back

        if (isAdded) {
            startActivityForResult(intent, requestCode)
        }
    }

}