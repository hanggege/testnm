package com.mei.red_packet

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import com.mei.base.common.BIND_PHONE_ACTION
import com.mei.login.ext.*
import com.mei.orc.callback.Callback
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.event.postAction
import com.mei.orc.http.exception.RxThrowable
import com.mei.orc.http.listener.RequestListener
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.showCountryCode
import com.mei.wood.extensions.subscribeBy
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.wood.ui.clickRedPacketDialogStat
import com.mei.wood.ui.redPacketBrowseDialog
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import com.net.network.chick.user.UpdatePhoneRequest
import com.trello.rxlifecycle3.android.FragmentEvent
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.bind_phone_bottom_dialog.*
import java.util.concurrent.TimeUnit

/**
 * 闪验绑定手机号底部弹框
 * @author Created by lenna on 2020/7/24
 */
class BindPhoneDialogFragment : BottomDialogFragment() {
    private var mCountry: String = ""
    private var mDownTask: Disposable? = null
    private var mPhone: String = ""
    private var mIsGetCode: Boolean = true
    private var mDownTime = 60
    var back: (Boolean) -> Unit = { _ -> }
    private var mFormatPhoneWatcher: FormatPhoneWatcher = FormatPhoneWatcher(object : FormatPhoneWatcher.OnFormatPhoneCallback {
        override fun onFormatPhone(phone: String, index: Int) {
            content_et.setText(phone)
            content_et.setSelection(index)
        }
    })

    override fun onStart() {
        super.onStart()
        dialog?.setCanceledOnTouchOutside(true)
        dialog?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View? = inflater.inflate(R.layout.bind_phone_bottom_dialog, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mDownTask?.dispose()
    }

    private fun initView() {
        setInputType(mIsGetCode)
        if (isAdded) {
            to_receive_red_packet_gcv.setOnClickNotDoubleListener(tag = "getTag", isNotDoubleClick = {
                optionsClickBtn()
            })
            bind_phone_country.setOnClickListener {
                activity?.showCountryCode(Callback { code -> bind_phone_country_tv.text = code })
            }

            content_et.formatPhoneNo(mFormatPhoneWatcher)
            down_time_tv.setOnClickListener {
                getCode()
            }
            close_iv.setOnClickListener {
                dismissAllowingStateLoss()
            }
        }

    }

    private fun optionsClickBtn() {
        if (mIsGetCode) {
            //获取验证码
            mPhone = content_et.text.trim().toString().replace(" ", "")
            if (TextUtils.isEmpty(mPhone)) {
                UIToast.toast("请输入手机号")
                return
            }
            to_receive_red_packet_gcv.isEnabled = false
            mCountry = bind_phone_country_tv.text?.toString().orEmpty()
            getCode()
        } else {
            //领取红包
            val code = content_et.text.trim().toString().replace(" ", "")
            if (TextUtils.isEmpty(code)) {
                UIToast.toast("请输入验证码")
                return
            }
            activity?.clickRedPacketDialogStat("立即领取")
            (activity as? MeiCustomBarActivity)?.showLoading(true)
            (activity as? MeiCustomBarActivity)?.apiSpiceMgr?.executeKt(UpdatePhoneRequest(mPhone
                    .joinCountryCode(mCountry), code, 1), success = {
                if (it.isSuccess) {
                    //绑定手机号成功
                    UIToast.toast(it.msg)
                    JohnUser.getSharedUser().sessionID = it.data.session_id
                    MeiUser.resetUser((activity as? MeiCustomBarActivity)?.apiSpiceMgr, object : RequestListener<ChickUserInfo.Response> {
                        override fun onRequestFailure(retrofitThrowable: RxThrowable?) {
                            dismissAllowingStateLoss()
                        }

                        override fun onRequestSuccess(result: ChickUserInfo.Response) {
                            back(true)
                            postAction(BIND_PHONE_ACTION)
                            (activity as? MeiCustomActivity)?.showReceiveRedPacketDialog()
                            dismissAllowingStateLoss()
                        }

                    })

                } else {
                    UIToast.toast(it.errMsg)
                }

            }, failure = {
                back(false)
                UIToast.toast(it?.currMessage)
            }, finish = {
                (activity as? MeiCustomBarActivity)?.showLoading(false)
            })
        }
    }

    /**
     * 获取验证码
     */
    @SuppressLint("SetTextI18n")
    private fun getCode() {
        (activity as? MeiCustomBarActivity)?.requestMobileCode(mCountry, mPhone, success = {
            if (isAdded) {
                to_receive_red_packet_gcv.isEnabled = true
                setInputType(false)
                mDownTime = 60
                down_time_tv.text = "${mDownTime}s"
                startDownTime()
                content_et.removeFormatPhoneNo(mFormatPhoneWatcher)
            }

        }, failure = { _, errorMsg ->
            if (isAdded)
                to_receive_red_packet_gcv.isEnabled = true
            UIToast.toast(errorMsg)
        })
    }

    /**
     * 改变dialog 业务类型
     */
    private fun setInputType(isGetCode: Boolean) {
        mIsGetCode = isGetCode
        if (mIsGetCode) {
            btn_content_tv.text = "获取验证码"
            content_et.hint = "请输入手机号"
            bind_phone_country.isVisible = true
            code_fl.isVisible = false
        } else {
            btn_content_tv.text = "立即领取"
            content_et.hint = "请输入验证码"
            content_et.setText("")
            bind_phone_country.isVisible = false
            code_fl.isVisible = true
            down_time_tv.isVisible = true
        }
    }

    /**
     * 启动获取验证码倒计时
     */
    @SuppressLint("SetTextI18n")
    private fun startDownTime() {
        Observable.interval(1, TimeUnit.SECONDS)
                .doOnSubscribe {
                    mDownTask = it
                }
                .observeOn(AndroidSchedulers.mainThread())
                .compose(bindUntilEvent(FragmentEvent.DESTROY_VIEW))
                .subscribeBy {
                    if (isAdded) {
                        if (mDownTime > 0) {
                            down_time_tv?.isEnabled = false
                            down_time_tv?.text = "${mDownTime}s"
                            mDownTime--
                        } else {
                            down_time_tv?.isEnabled = true
                            down_time_tv?.text = "获取验证码"
                            mDownTask?.dispose()
                        }
                    }
                }

    }
}

fun FragmentActivity.showBindPhoneDialog(backResult: (Boolean) -> Unit = { _ -> }) {
    (this as? MeiCustomActivity)?.redPacketBrowseDialog()
    val dialog = BindPhoneDialogFragment()
            .apply {
                back = backResult
            }
    dialog.show(supportFragmentManager, "BindPhoneDialogFragment")
}

fun FragmentActivity.dismissBindPhoneDialog() {
    (supportFragmentManager
            .findFragmentByTag("BindPhoneDialogFragment")
            as? BindPhoneDialogFragment)?.dismissAllowingStateLoss()
}