package com.mei.chat.ui.menu.action

import android.Manifest
import androidx.fragment.app.FragmentActivity
import com.mei.im.ui.fragment.ImVideoInputDialogV2
import com.mei.orc.rxpermission.requestMulPermissionZip
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R

/**
 *  Created by zzw on 2019/3/18
 *  Des:视频
 *  IMVideoInputDialog 内部使用 getActivity().startActivityForResult(intent, IMConstantsKt.REQUEST_CODE_VIDEO);
 *  所以在对应的activity通过onActivityResult获取对应的视频地址
 */


class VideoAction : BaseAction(R.drawable.im_menu_video,
        "视频") {

    override fun onClick() {
        fragment?.context?.let { context ->
            (context as? FragmentActivity)?.requestMulPermissionZip(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.CAMERA) {
                if (it) {
                    fragment?.childFragmentManager?.let { manager ->
                        ImVideoInputDialogV2().show(manager, "VideoInputDialog")
                    }
                } else {
                    UIToast.toast(context, R.string.request_camera_sdcard_permission)
                }
            }
        }

    }
}