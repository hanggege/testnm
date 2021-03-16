package com.mei.orc.util.startactivity

import android.content.Intent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-13
 * 针对startActivityForResult 封装
 */


fun FragmentActivity.startFragmentForResult(intent: Intent, requestCode: Int = 0, back: (requestCode: Int, data: Intent?) -> Unit = { _, _ -> }) {
    registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        back(requestCode, it.data)
    }.launch(intent)
}
