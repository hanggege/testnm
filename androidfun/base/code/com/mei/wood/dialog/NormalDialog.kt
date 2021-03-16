package com.mei.wood.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.orc.dialog.MeiSupportDialogFragment
import com.mei.orc.ext.runDelayedOnUiThread
import com.mei.orc.unit.TimeUnit
import com.mei.orc.util.handler.GlobalUIHandler
import com.mei.wood.R
import com.mei.wood.util.AppManager
import kotlinx.android.synthetic.main.mei_normal_dialog.*
import launcher.MakeResult
import java.util.*
import kotlin.math.round

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/8/24
 */


/**
 * 回调
 */
interface DialogBack {
    fun handle(state: Int)
}

/**
 * 仅仅Ok时返回
 */
interface DialogOkBack {
    fun okHandle()
}

const val DISSMISS_DO_NOTHING = 2 //只是消息，未做任何处理
const val DISSMISS_DO_OK = 1 //点击右边的确定
const val DISSMISS_DO_CANCEL = 0//点击左边的取消


/**
 * 待弹窗的队列
 */
private val queueDataList = PriorityQueue<QueueData>(3, Comparator<QueueData> { e1, e2 -> e2.priority - e1.priority })

private data class QueueData(
        val priority: Int = 0,//优先级 越大越高
        val time: Long = System.currentTimeMillis(),//入队时间戳
        val provider: (topActivity: FragmentActivity, NormalDialog) -> DialogData?
)

/**
 * provider 如果不需要展示了，provider返回null即可
 */
fun pushDialogQueueAndTryShow(priority: Int = 0, provider: (topActivity: FragmentActivity, dialog: NormalDialog) -> DialogData?) {
    queueDataList.add(QueueData(priority = priority, provider = provider))
    showNextDialog()
}

/**
 * 情况队列
 */
fun clearDialogDialog() {
    queueDataList.clear()
}

/**
 * 展示下一条
 */
fun showNextDialog() {
    val data = queueDataList.peek()
    data?.let {
        val topActivity = AppManager.getInstance().currentActivity()
        (topActivity as? FragmentActivity)?.let { activity ->
            val dialog = NormalDialogLauncher.newInstance()
            val dialogData = it.provider(activity, dialog)

            if (dialogData == null) {//如果由于一些判断，返回了null，那么就当不显示这个数据了
                queueDataList.poll()
                showNextDialog()
            } else {
                val tag = if (dialogData.key.isEmpty()) NormalDialog::class.java.simpleName else dialogData.key
                if (activity.supportFragmentManager.findFragmentByTag(tag) == null) {
                    queueDataList.poll()
                    dialog.showDialog(activity, dialogData) {
                        runDelayedOnUiThread(500) {
                            showNextDialog()
                        }
                    }
                }
            }
        }
    }
}

@MakeResult
class NormalDialog : MeiSupportDialogFragment() {

    private var dialogBack: DialogBack? = null
    private var okBack: DialogOkBack? = null
    var data: DialogData? = null

    private var dismissRunnable: Runnable? = null
    private var countDownTimer: CountDownTimer? = null

    override fun onSetInflaterLayout(): Int {
        return R.layout.mei_normal_dialog
    }

    override fun ignoreBackground(): Boolean {
        return data?.ignoreBackground ?: false
    }

    override fun onStart() {
        super.onStart()
        if (data?.isCustomViewGravityTop == true) {
            dialog?.window?.apply {
                val params = attributes
                params.gravity = Gravity.TOP
                params.width = WindowManager.LayoutParams.MATCH_PARENT
                attributes = params
                setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                if (data?.backCanceled == true) {
                    super.onBackPressed()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (data == null) dismissAllowingStateLoss()
        data?.let {
            dialog?.setCanceledOnTouchOutside(it.canceledOnTouchOutside)
            if (it.customView != null) {
                custom_panel.isVisible = true
                dialog_layout.isVisible = false
                custom_panel.addView(it.customView)
            } else {
                custom_panel.isVisible = false
                dialog_layout.isVisible = true
                cancel_btn.setOnClickListener {
                    dialogBack?.handle(DISSMISS_DO_CANCEL)
                    dialogBack = null
                    dismissAllowingStateLoss()
                }
                ok_btn.setOnClickListener {
                    dialogBack?.handle(DISSMISS_DO_OK)
                    okBack?.okHandle()
                    dialogBack = null
                    okBack = null
                    dismissAllowingStateLoss()
                }

                dialog_content_hint.isVisible = it.messageHint.orEmpty().isNotEmpty()
                dialog_content_hint.text = it.messageHint

                dialog_title.paint.isFakeBoldText = true
                dialog_title.isVisible = it.title.orEmpty().isNotEmpty()
                dialog_title.text = it.title
                dialog_content.text = it.message
                cancel_btn.isVisible = it.cancelStr.orEmpty().isNotEmpty()
                cancel_btn.text = it.cancelStr
                cancel_btn.setTextColor(it.cancelColor)

                ok_btn.isVisible = it.okStr.orEmpty().isNotEmpty()
                ok_btn.text = it.okStr
                ok_btn.setTextColor(it.okColor)

                dialog_bottom_layout.isVisible = it.okStr.orEmpty().isNotEmpty() || it.cancelStr.orEmpty().isNotEmpty()
                mid_line.isVisible = it.okStr.orEmpty().isNotEmpty() || it.cancelStr.orEmpty().isNotEmpty()

                action_line.isVisible = it.cancelStr.orEmpty().isNotEmpty() && it.okStr.orEmpty().isNotEmpty()
            }

            if (it.timeOut > 0) {
                dismissRunnable = Runnable { dismissAllowingStateLoss() }
                GlobalUIHandler.postUiDelay(dismissRunnable, (it.timeOut * 1000).toLong())
            }

            if (it.okTimeOut > 0) {
                countDownTimer = object : CountDownTimer(it.okTimeOut * TimeUnit.SECOND, TimeUnit.SECOND) {
                    @SuppressLint("SetTextI18n")
                    override fun onTick(millisUntilFinished: Long) {
                        if (isAdded) {
                            ok_btn.text = "${it.okStr}（${round(millisUntilFinished * 1f / TimeUnit.SECOND).toInt()}S）"
                        }
                    }

                    override fun onFinish() {
                        countDownTimer?.cancel()
                        cancel_btn.performClick()
                    }
                }
                countDownTimer?.start()
            }

        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        dismissRunnable?.apply {
            GlobalUIHandler.remove(this)
        }
        countDownTimer?.cancel()
        dialogBack?.handle(DISSMISS_DO_NOTHING)
    }

    private fun showPrivateDialog(activity: FragmentActivity?, data: DialogData, back: DialogBack? = null, okBack: DialogOkBack? = null): NormalDialog {
        if (activity != null) {
            this.data = data
            this.dialogBack = back
            this.okBack = okBack
            val tag = if (data.key.isEmpty()) NormalDialog::class.java.simpleName else data.key
            show(activity.supportFragmentManager, tag)
        }
        return this
    }


    /****************************提供外面使用************************************/


    @JvmOverloads
    fun showDialog(activity: FragmentActivity?, msg: String, okBack: () -> Unit = {}, back: (Int) -> Unit = {}): NormalDialog {
        return showDialog(activity, DialogData(msg), okBack, back)
    }

    @JvmOverloads
    fun showDialog(activity: FragmentActivity?, data: DialogData, okBack: () -> Unit = {}, back: (Int) -> Unit = {}): NormalDialog {
        return showPrivateDialog(activity, data,
                object : DialogBack {
                    override fun handle(state: Int) {
                        back(state)
                    }

                },
                object : DialogOkBack {
                    override fun okHandle() {
                        okBack()
                    }
                })
    }

    fun showDialog(activity: FragmentActivity?, msg: String, okBack: DialogOkBack? = null): NormalDialog {
        return showDialog(activity, DialogData(msg), okBack)
    }

    fun showDialog(activity: FragmentActivity?, data: DialogData, okBack: DialogOkBack? = null): NormalDialog {
        return showPrivateDialog(activity, data, null, okBack)

    }

    fun showDialog(activity: FragmentActivity?, msg: String, back: DialogBack? = null): NormalDialog {
        return showDialog(activity, DialogData(msg), back)
    }

    fun showDialog(activity: FragmentActivity?, data: DialogData, back: DialogBack? = null): NormalDialog {
        return showPrivateDialog(activity, data, back, null)
    }


}