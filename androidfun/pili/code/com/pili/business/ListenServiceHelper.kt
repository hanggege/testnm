package com.pili.business

import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.pili.pldroid.player.PLOnErrorListener

/**
 * @author caowei
 * @email 646030315@qq.com
 * Created on 17/7/7.
 */

fun handleErrorCode(errorCode: Int) {
    when (errorCode) {
        PLOnErrorListener.ERROR_CODE_OPEN_FAILED -> UIToast.toast(R.string.errcode_player_open_failed)
//        PLOnErrorListener.ERROR_CODE_IO_ERROR -> UIToast.toast(R.string.errcode_io_error)
        PLOnErrorListener.ERROR_CODE_SEEK_FAILED -> UIToast.toast(R.string.errcode_seek_error)
        PLOnErrorListener.ERROR_CODE_HW_DECODE_FAILURE -> UIToast.toast(R.string.errcode_hw_decode_failure)
//        PLMediaPlayer.ERROR_CODE_PLAYER_DESTROYED -> UIToast.toast(R.string.errcode_player_destroyed)
        PLOnErrorListener.ERROR_CODE_PLAYER_VERSION_NOT_MATCH -> UIToast.toast(R.string.errcode_version_not_match)
        PLOnErrorListener.MEDIA_ERROR_UNKNOWN -> UIToast.toast("未知错误")
    }
}