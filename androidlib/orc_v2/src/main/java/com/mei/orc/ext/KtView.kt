package com.mei.orc.ext

import android.view.View

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/13
 */

inline fun View.findRootParent(condition: (view: View?) -> Boolean = { _ -> true }): View? {
    var parentView: View? = parent as? View
    while (parentView?.parent is View && condition(parentView)) {
        parentView = parentView.parent as? View
    }
    return parentView
}