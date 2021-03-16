package com.mei.me.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.FragmentActivity
import com.mei.dialog.showUploadHeaderDialog
import com.mei.me.ext.uploadAvatar
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.network.chick.upload.CheckAvatarViolationRequest
import com.net.network.chick.user.UserEditRequest
import kotlinx.android.synthetic.main.view_upload_tip_dialog.*

/**
 * author : Song Jian
 * date   : 2020/1/9
 * desc   : 上传头像提示框
 */
fun FragmentActivity.showUploadViaDialog(callBack: (type: Int) -> Unit = { _ -> }) {
    UploadViaDialog().apply {
        this.callBack = callBack
    }.show(supportFragmentManager, "UploadViaDialog")
}

class UploadViaDialog : BottomDialogFragment() {

    var callBack: (type: Int) -> Unit = { _ -> }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_upload_tip_dialog, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initView()
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            val params = attributes
            params.gravity = Gravity.CENTER
            params.width = WindowManager.LayoutParams.WRAP_CONTENT
            params.dimAmount = getBackgroundAlpha()
            attributes = params
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
    }

    private fun initView() {
        upload_avatar_now.setOnClickListener {
            uploadHeader()
            dismiss()
        }
        complement_info_cancel.setOnClickListener {
            dismiss()
        }
    }


    private fun uploadHeader() {
        (activity as? MeiCustomActivity)?.apply {
            showUploadHeaderDialog { _, extra ->
                showLoading(true)
                uploadAvatar(extra) { url ->
                    if (url.isNullOrEmpty()) {
//                UIToast.toast("上传失败")
                        showLoading(false)
                    } else {
                        apiSpiceMgr.executeToastKt(CheckAvatarViolationRequest(url), success = {
                            if (it.isSuccess) {
                                if (it.data?.isViolation == false) {
//                                    val photoList = MeiUser.getSharedUser().info?.photos.orEmpty().map { it.photoUrl } as? ArrayList
                                    apiSpiceMgr.executeKt(UserEditRequest().apply {
                                        /*       photos = photoList?.apply {
                                                   if (isNotEmpty()) {
                                                       set(0, url)
                                                   } else {
                                                       add(url)
                                                   }
                                               }*/
                                    }, success = {
                                        if (it.isSuccess) {
                                            UIToast.toast("上传成功 等待审核")
                                        } else {
                                            UIToast.toast(it.errMsg)
                                        }
                                    }, failure = {
                                        UIToast.toast(it?.currMessage)
                                    }, finish = {
                                        showLoading(false)
                                    })
                                } else {
                                    /**图片违规，重新拉起上传头像弹窗*/
                                    UIToast.toast(it.errMsg)
                                    uploadHeader()
                                }
                            }

                        })
                    }
                }
            }
        }

    }
}