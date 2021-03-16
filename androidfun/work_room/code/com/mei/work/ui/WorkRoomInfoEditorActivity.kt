package com.mei.work.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.mei.dialog.showUploadHeaderDialog
import com.mei.me.ext.uploadAvatar
import com.mei.orc.ActivityLauncher
import com.mei.orc.Cxt
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.click.setOnClickNotDoubleListener
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.wood.R
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.model.chick.workroom.WorkRoomInfo
import com.net.network.chick.workroom.WorkRoomInfoRequest
import kotlinx.android.synthetic.main.activity_work_info_editor.*
import kotlinx.android.synthetic.main.include_net_error_layout.*
import launcher.MakeResult

/**
 * 工作室信息编辑页
 * @author Created by lenna on 2020/7/22
 */
@MakeResult(includeStartForResult = false)
class WorkRoomInfoEditorActivity : MeiCustomBarActivity() {
    var mWorkRoomInf: WorkRoomInfo = WorkRoomInfo()
    var mTags: String? = null
    var mAvatarUrl: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_work_info_editor)
        ActivityLauncher.bind(this)
        title = "工作室资料"
        requestData()
        initView()
    }

    override fun onClickLeft(view: View) {
        checkChange()
    }

    override fun onBackPressed() {
        checkChange()
    }

    private fun checkChange() {
        if (checkChanged()) {
            NormalDialogLauncher.newInstance().showDialog(this, "当前修改未保存,是否返回？", {
                setResult(Activity.RESULT_OK, Intent())
                finish()
            })
        } else {
            setResult(Activity.RESULT_OK, Intent())
            finish()
        }
    }

    private fun checkChanged(): Boolean {

        if (work_room_nickname_et.text.toString() != mWorkRoomInf.info?.nickname
                || work_room_introduce_cet.content != mWorkRoomInf.info?.introduction
                || mTags != mWorkRoomInf.info?.tags?.joinToString { "," }
                || mAvatarUrl != mWorkRoomInf.info?.avatar) {
            return true
        }
        return false
    }


    fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(WorkRoomInfoRequest(), success = {
            if (it.isSuccess) {
                net_error_layout.isVisible = false
                mWorkRoomInf = it.data ?: WorkRoomInfo()
                loadWorkRoomInfo()
            } else {
                net_error_layout.apply {
                    isVisible = true
                    setBtnText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.try_again) else "立即登录")
                    setEmptyText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.no_network) else "登录后才能编辑")
                }
            }
        }, failure = {
            net_error_layout.apply {
                isVisible = true
                setBtnText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.try_again) else "立即登录")
                setEmptyText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.no_network) else "登录后才能编辑")
            }
        }, finish = {
            showLoading(false)
        })
    }

    private fun initView() {
        et_custom_tag.setOnClickListener {
            addTag()
        }
        save_work_info_gcv.setOnClickNotDoubleListener(tag = "save_work_info_gcv") {
            saveWorkRoomInfo()
        }
        et_work_room_introduce.setOnClickNotDoubleListener(tag = "et_work_room_introduce") {
            startFragmentForResult(EditContentActivityLauncher.getIntentFrom(this
                    , mWorkRoomInf.info?.introduction
                    , "工作室简介"), 1000) { code, data ->
                if (code == 1000) {
                    val introduction = data?.getStringExtra(RESULT_EDIT_CONTENT)
                    if (introduction?.isEmpty() != true) {
                        work_room_introduce_cet.content = introduction
                    }
                }
            }
        }
        et_member_introduce.setOnClickNotDoubleListener(tag = "et_member_introduce") {
            WorkRoomMemberIntroduceEditActivityLauncher.startActivity(this)
        }
        work_room_img_riv.setOnClickListener {
            uploadHeader()
        }

    }

    /**
     * 上传头像
     */
    private fun uploadHeader() {
        showUploadHeaderDialog { _, extra ->
            if (extra?.isNotEmpty() == true) {
                showLoading(true)
            }
            uploadAvatar(extra, back = {
                if (it?.isNotEmpty() == true) {
                    mAvatarUrl = it
                    work_room_img_riv.glideDisplay(mAvatarUrl)
                }
                showLoading(false)
            })

        }
    }


}