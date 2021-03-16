package com.mei.work.ui

import androidx.core.view.isVisible
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.mei.work.WorkRoomCustomTagAdapter
import com.net.network.chick.workroom.UpdateWorkRoomInfoRequest
import kotlinx.android.synthetic.main.activity_work_info_editor.*

/**
 *
 * @author Created by lenna on 2020/7/22
 * 工作室编辑信息扩展文件
 */

fun WorkRoomInfoEditorActivity.loadWorkRoomInfo() {
    work_room_img_riv.glideDisplay(mWorkRoomInf.info?.avatar, R.drawable.default_avatar_72, R.drawable.default_avatar_72)
    work_room_nickname_et.setText(mWorkRoomInf.info?.nickname.orEmpty())
    work_room_nickname_et.setSelection(work_room_nickname_et.text.length)
    work_room_nickname_et.requestFocus()
    work_room_introduce_cet.content = mWorkRoomInf.info?.introduction
    mTags = mWorkRoomInf.info?.tags?.joinToString { "," }
    mAvatarUrl = mWorkRoomInf.info?.avatar
    custom_tag_tfl.adapter = WorkRoomCustomTagAdapter(mWorkRoomInf.info?.tags
            ?: arrayListOf(), refresh = { count ->
        refreshTagCount(count)
    })
    refreshTagCount(10 - (mWorkRoomInf.info?.tags?.size ?: 0))
}

private fun WorkRoomInfoEditorActivity.refreshTagCount(count: Int) {
    custom_tag_hint.isVisible = mWorkRoomInf.info?.tags?.size ?: 0 < 10
    val hint = String.format(Cxt.getStr(R.string.work_room_hint_custom_tag), count)
    custom_tag_hint.text = hint
}

fun WorkRoomInfoEditorActivity.addTag() {
    showAddTagDialog(callBack = {
        if (mWorkRoomInf.info?.tags?.size ?: 0 >= 10) {
            UIToast.toast("最多只能添加10个标签")
            return@showAddTagDialog
        }
        if (it.isEmpty()) {
            UIToast.toast("请输入标签")
            return@showAddTagDialog
        }
        if (mWorkRoomInf.info?.tags?.contains(it) == true) {
            UIToast.toast("不能和已有标签重复")
            return@showAddTagDialog
        }
        if (it.length < 2) {
            UIToast.toast("标签至少两个字")
            return@showAddTagDialog
        }
        (custom_tag_tfl.adapter as? WorkRoomCustomTagAdapter)?.addItem(it)
    })
}

fun WorkRoomInfoEditorActivity.saveWorkRoomInfo() {
    val nickName = work_room_nickname_et.text?.toString()
    val introduction: String? = work_room_introduce_cet.content
    if (nickName?.isEmpty() == true) {
        UIToast.toast("请输入昵称")
        return
    }
    if (introduction?.isEmpty() == true) {
        UIToast.toast("请编辑工作室简介")
        return
    }
    mWorkRoomInf.info?.avatar = mAvatarUrl
    //自定义标签已去掉拦截，把错误提示交给后端配置

    updateWorkRoomInfo(mWorkRoomInf.info?.avatar, nickName, mWorkRoomInf.info?.tags?.joinToString(","), introduction)

}

fun WorkRoomInfoEditorActivity.updateWorkRoomInfo(avatar: String?, nickName: String?, tags: String?, introduction: String?) {
    showLoading(true)
    apiSpiceMgr.executeKt(UpdateWorkRoomInfoRequest(avatar, nickName, tags, introduction), success = {
        if (it.isSuccess) {
            UIToast.toast("保存成功")
            mWorkRoomInf.info?.introduction = introduction
            requestData()
        } else {
            UIToast.toast(it.errMsg)
        }
    }, failure = {
        UIToast.toast(it?.currMessage)
    }, finish = { showLoading(false) })
}