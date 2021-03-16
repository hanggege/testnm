package com.mei.browser

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.mei.browser.ui.ImageBrowserFragment

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-09-24
 */

fun Context.showImageBrowser(info: BrowserInfo) {
    (this as? FragmentActivity)?.apply {
        ImageBrowserFragment().apply {
            browserInfo = info
            show(supportFragmentManager, "ImageBrowserFragment")
        }
    }
}