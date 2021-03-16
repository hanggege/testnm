package com.mei.im.ui.dialog

import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.mei.orc.ActivityLauncher
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.wood.R
import kotlinx.android.synthetic.main.fragment_chat_dialog_layout.*
import launcher.Boom
import launcher.MulField

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/4/22
 */

fun FragmentActivity?.showChatDialog(chatId: String = "",from: String = "") {
    this?.supportFragmentManager?.let { manager ->
        IMChatFragmentDialogLauncher.newInstance(chatId,from).show(manager, "")
    }
}

class IMChatFragmentDialog : BottomDialogFragment(), View.OnLayoutChangeListener {
    @Boom
    var chatId: String = ""

    @Boom
    @MulField
    var from = ""

    /** 键盘的状态，第一个数据是最新Bottom位置值，第二个是键盘状态 **/
    private var mKeyBoradState = Pair(0, false)

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onResume() {
        super.onResume()
        onBackPressed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_chat_dialog_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ActivityLauncher.bind(this)
        childFragmentManager.commit(true) {
            setCustomAnimations(0, R.anim.fragment_slide_left_out, R.anim.fragment_slide_left_in, R.anim.fragment_slide_right_out)
            if (chatId.isEmpty()) {
                replace(R.id.chat_container, IMConversationFragmentDialog())
                addToBackStack("IMConversationFragmentDialog")
            } else {
                replace(R.id.chat_container, IMMessageFragmentDialogLauncher.newInstance(chatId, "").apply {
                    this.backDismissCallback = {
                        dismissAllowingStateLoss()
                    }
                })
                addToBackStack("IMMessageFragmentDialog")
            }
        }
        chat_bg.setOnClickListener {
            if (childFragmentManager.backStackEntryCount == 1) dismissAllowingStateLoss()
            else childFragmentManager.popBackStackImmediate()
        }
        chat_container.addOnLayoutChangeListener(this)
        onBackPressed()
    }

    override fun onLayoutChange(v: View?, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int) {
        v?.let {
            val isShow = oldBottom - bottom > dip(100)
            mKeyBoradState = Pair(bottom, isShow)
        }

    }

    /**
     * 返回键监听
     */
    private fun onBackPressed() {
        dialog?.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK
                    /** 是否到达最后一个 **/
                    && childFragmentManager.backStackEntryCount > 1
                    /** 键盘状态是否弹起 **/
                    && !mKeyBoradState.second) {
                childFragmentManager.popBackStackImmediate()
                true
            } else {
                false
            }
        }
    }
}
