package com.mei.base.weight.kps

import android.view.View

/**
 *
 * @author Modify by Ling on 2019-07-05
 */
/**
 * This listener is used to listening the click event for a view which is received the click event to switch between Panel and
 * Keyboard.
 *
 * @see attach(View, View, View, SwitchClickListener)
 */
interface SwitchClickListener {
    /**
     * @param switchToPanel If true, switch to showing Panel; If false, switch to showing Keyboard.
     */
    fun onClickSwitch(clickView: View, switchToPanel: Boolean)
}