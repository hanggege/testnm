package com.mei.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.FragmentActivity
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.picker.cropImage
import com.mei.picker.tokePhotoAndCrop
import com.mei.wood.R
import kotlinx.android.synthetic.main.view_upload_header.*

/**
 *  Created by zzw on 2019-11-29
 *  Des: 上传头像对话框
 */


fun FragmentActivity.showUploadHeaderDialog(callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }) {
    UploadHeaderDialog().apply {
        this.callBack = callBack
    }.show(supportFragmentManager, "UploadHeaderDialog")
}


class UploadHeaderDialog : BottomDialogFragment() {
    var callBack: (type: Int, extra: String?) -> Unit = { _, _ -> }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_upload_header, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    @Suppress("DEPRECATION")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //从手机相册选择
        dialog_sel_album.setOnClickListener {
            dismissAllowingStateLoss()
            activity?.cropImage { avatarUrl ->
                if (avatarUrl.isNotEmpty()) {
                    callBack(1, avatarUrl)
                    dismissAllowingStateLoss()
                }
            }
        }
        //拍照上传
        dialog_sel_camera.setOnClickListener {
            activity?.tokePhotoAndCrop() { avatarUrl ->
                if (avatarUrl.orEmpty().isNotEmpty()) {
                    callBack(2, avatarUrl)
                    dismissAllowingStateLoss()
                }
            }
            callBack(2, null)
            dismissAllowingStateLoss()
        }
        dialog_sel_cancel.setOnClickListener {
            dismissAllowingStateLoss()
        }
    }

}