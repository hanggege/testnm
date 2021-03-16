package com.mei.orc.util.ui

import android.view.View
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible

/**
 *
 * @author Created by Ling on 2019-08-27
 */

fun setViewInvisible(invisible: Boolean, vararg views: View?): Boolean {
    for (view in views) {
        view?.isInvisible = invisible
    }
    return invisible
}

fun setViewVisibleOrGone(visibleCondition: Boolean, vararg views: View?): Boolean {
    for (view in views) {
        view?.isVisible = visibleCondition
    }
    return visibleCondition
}

fun setViewGone(gone: Boolean, vararg views: View?): Boolean {
    for (view in views) {
        view?.isGone = gone
    }
    return gone
}