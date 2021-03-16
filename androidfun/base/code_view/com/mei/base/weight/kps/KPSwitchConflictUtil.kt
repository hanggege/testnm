package com.mei.base.weight.kps

import android.annotation.TargetApi
import android.app.Activity
import android.os.Build
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import cn.dreamtobe.kpswitch.handler.KPSwitchPanelLayoutHandler
import cn.dreamtobe.kpswitch.util.KeyboardUtil
import cn.dreamtobe.kpswitch.util.ViewUtil

/**
 *
 * @author Modify by Ling on 2019-07-05
 * Created by Jacksgong on 3/30/16.
 * <p/>
 * This util will help you control your panel and keyboard easily and exactly with non-layout-conflict.
 * <p/>
 * This util just support the application layer encapsulation, more detail for how to resolve the layout-conflict please Ref
 * {@link [KPSwitchRootLayoutHandler]}、 [KPSwitchPanelLayoutHandler]、[KPSwitchFSPanelLayoutHandler]
 * <p/>
 * Any problems: https://github.com/Jacksgong/JKeyboardPanelSwitch
 * @see KPSwitchRootLayoutHandler
 * @see KPSwitchPanelLayoutHandler
 * @see KPSwitchFSPanelLayoutHandler
 */

/**
 * Attach the action of {@code switchPanelKeyboardBtn} and the {@code focusView} to non-layout-conflict.
 * <p/>
 * You do not have to use this method to attach non-layout-conflict, in other words, you can attach the action by yourself with invoke
 * methods manually: {@link #showPanel(View)}、 {@link #showKeyboard(View, View)}、{@link #hidePanelAndKeyboard(View)}, and in the case of
 * don't invoke this method to attach, and if your activity with the fullscreen-theme, please ensure your panel layout is {@link
 * View#INVISIBLE} before the keyboard is going to show.
 *
 * @param panelLayout            the layout of panel.
 * @param switchPanelKeyboardBtn the view will be used to trigger switching between the panel and the keyboard.
 * @param focusView              the view will be focused or lose the focus.
 * @param switchClickListener    the click listener is used to listening the click event for {@code switchPanelKeyboardBtn}.
 * @see attach(View, View, SwitchClickListener, SubPanelAndTrigger...)
 */
fun attach(panelLayout: View, switchPanelKeyboardBtn: View?, focusView: View?, switchClickListener: SwitchClickListener? = null) {
    switchPanelKeyboardBtn?.setOnClickListener {
        val switchToPanel = switchPanelAndKeyboard(panelLayout, focusView)
        switchClickListener?.onClickSwitch(switchPanelKeyboardBtn, switchToPanel)
    }

    if ((panelLayout.context as? Activity).isHandleByPlaceholder()) {
        focusView?.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                /**
                 * Show the fake empty keyboard-same-height panel to fix the conflict when
                 * keyboard going to show.
                 * @see showKeyboard(View, View)
                 */
                panelLayout.isInvisible = true
            }
            return@setOnTouchListener false
        }
    }
}

/**
 * If you have multiple sub-panels in the {@code panelLayout}, you can use this method to simply attach them to non-layout-conflict.
 * otherwise you can use {@link #attach(View, View, View)} or {@link #attach(View, View, View, SwitchClickListener)}.
 *
 * @param panelLayout         the layout of panel.
 * @param focusView           the view will be focused or lose the focus.
 * @param switchClickListener the listener is used to listening whether the panel is showing or keyboard is showing with toggle the
 *                            panel/keyboard state.
 * @param subPanelAndTriggers the array of the trigger-toggle-view and the sub-panel which bound trigger-toggle-view.
 */
fun attach(panelLayout: View, focusView: View?, switchClickListener: SwitchClickListener? = null, vararg subPanelAndTriggers: SubPanelAndTrigger) {
    subPanelAndTriggers.forEach {
        bindSubPanel(it, subPanelAndTriggers, focusView, panelLayout, switchClickListener)
    }

    if ((panelLayout.context as? Activity).isHandleByPlaceholder()) {
        focusView?.setOnTouchListener { _, motionEvent ->
            if (motionEvent.action == MotionEvent.ACTION_UP) {
                /**
                 * Show the fake empty keyboard-same-height panel to fix the conflict when
                 * keyboard going to show.
                 * @see showKeyboard(View, View)
                 */
                panelLayout.isInvisible = true
            }
            return@setOnTouchListener false
        }
    }
}

/**
 * To show the panel(hide the keyboard automatically if the keyboard is showing) with non-layout-conflict.
 *
 * @param panelLayout the layout of panel.
 * @see KPSwitchPanelLayoutHandler
 */
fun showPanel(panelLayout: View) {
    val activity = (panelLayout.context as? Activity)
    panelLayout.isVisible = true
    if (activity?.currentFocus != null) {
        KeyboardUtil.hideKeyboard(activity.currentFocus)
    }
}

/**
 * To show the keyboard(hide the panel automatically if the panel is showing) with non-layout-conflict.
 *
 * @param panelLayout the layout of panel.
 * @param focusView   the view will be focused.
 */
fun showKeyboard(panelLayout: View, focusView: View?) {
    val activity = (panelLayout.context as? Activity)
    if (focusView != null)
        KeyboardUtil.showKeyboard(focusView)
    if (activity.isHandleByPlaceholder()) {
        panelLayout.isInvisible = true
    }
}

/**
 * If the keyboard is showing, then going to show the {@code panelLayout}, and hide the keyboard with non-layout-conflict.
 * <p/>
 * If the panel is showing, then going to show the keyboard, and hide the {@code panelLayout} with non-layout-conflict.
 * <p/>
 * If the panel and the keyboard are both hiding. then going to show the {@code panelLayout} with non-layout-conflict.
 *
 * @param panelLayout the layout of panel.
 * @param focusView   the view will be focused or lose the focus.
 * @return If true, switch to showing {@code panelLayout} If false, switch to showing Keyboard.
 */
fun switchPanelAndKeyboard(panelLayout: View, focusView: View?): Boolean {
    val switchToPanel = panelLayout.isVisible
    if (!switchToPanel) {
        showKeyboard(panelLayout, focusView)
    } else {
        showPanel(panelLayout)
    }

    return switchToPanel
}

/**
 * Hide the panel and the keyboard.
 *
 * @param panelLayout the layout of panel.
 */
fun hidePanelAndKeyboard(panelLayout: View) {
    val activity = (panelLayout.context as? Activity)

    val focusView = activity?.currentFocus
    if (focusView != null) {
        KeyboardUtil.hideKeyboard(focusView)
        focusView.clearFocus()
    }
    panelLayout.isVisible = false
}

/**
 * @param isFullScreen        Whether in fullscreen theme.
 * @param isTranslucentStatus Whether in translucent status theme.
 * @param isFitsSystem        Whether the root view(the child of the content view) is in {@code getFitSystemWindow()} equal true.
 * @return Whether handle the conflict by show panel placeholder, otherwise, handle by delay the visible or gone of panel.
 */
fun isHandleByPlaceholder(isFullScreen: Boolean, isTranslucentStatus: Boolean, isFitsSystem: Boolean): Boolean {
    return isFullScreen || (isTranslucentStatus && !isFitsSystem)
}

fun Activity?.isHandleByPlaceholder(): Boolean {
    return if (this == null) true
    else
        isHandleByPlaceholder(ViewUtil.isFullScreen(this),
                ViewUtil.isTranslucentStatus(this), isFitsSystemWindows())
}

private fun bindSubPanel(subPanelAndTrigger: SubPanelAndTrigger, subPanelAndTriggers: Array<out SubPanelAndTrigger>,
                         focusView: View?, panelLayout: View, switchClickListener: SwitchClickListener?) {

    val triggerView = subPanelAndTrigger.triggerView
    val boundTriggerSubPanelView = subPanelAndTrigger.subPanelView

    triggerView.setOnClickListener {
        val switchToPanel =
                if (panelLayout.isVisible) {
                    // panel is visible.

                    if (boundTriggerSubPanelView.isVisible) {
                        // bound-trigger panel is visible.
                        // to show keyboard.
                        showKeyboard(panelLayout, focusView)
                        false
                    } else {
                        // bound-trigger panel is invisible.
                        // to show bound-trigger panel.
                        showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers)
                        true
                    }
                } else {
                    // panel is gone.
                    // to show panel.
                    showPanel(panelLayout)
                    // to show bound-trigger panel.
                    showBoundTriggerSubPanel(boundTriggerSubPanelView, subPanelAndTriggers)
                    true
                }

        switchClickListener?.onClickSwitch(it, switchToPanel)
    }
}

private fun showBoundTriggerSubPanel(boundTriggerSubPanelView: View, subPanelAndTriggers: Array<out SubPanelAndTrigger>) {
    // to show bound-trigger panel.
    subPanelAndTriggers.forEach {
        if (it.subPanelView != boundTriggerSubPanelView) {
            // other sub panel.
            it.subPanelView.isVisible = false
        }
    }
    boundTriggerSubPanelView.isVisible = true
}

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
fun Activity?.isFitsSystemWindows(): Boolean {
    //noinspection SimplifiableIfStatement
    return this?.findViewById<ViewGroup>(android.R.id.content)?.getChildAt(0)?.fitsSystemWindows
            ?: false
}


/**
 * @see #attach(View, View, SwitchClickListener, SubPanelAndTrigger...)
 * The sub-panel view is the child of panel-layout.
 * The trigger view is used for triggering the {@code subPanelView} VISIBLE state.
 */
data class SubPanelAndTrigger(var subPanelView: View, var triggerView: View)