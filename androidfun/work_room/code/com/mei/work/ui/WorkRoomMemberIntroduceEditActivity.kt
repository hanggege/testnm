package com.mei.work.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.mei.base.common.CHANG_LOGIN
import com.mei.keyboard.keyboardBindLife
import com.mei.login.toLogin
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.screenHeight
import com.mei.orc.john.model.JohnUser
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.R
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.mei.work.adapter.MemberListAdapter
import com.net.model.chick.workroom.WorkRoomMember
import com.net.network.chick.workroom.EditMemberIntroductionRequest
import com.net.network.chick.workroom.WorkRoomMemberInfoRequest
import kotlinx.android.synthetic.main.activity_member_introduce_edit.*
import kotlinx.android.synthetic.main.include_net_error_layout.*
import launcher.MakeResult

/**
 *
 * @author Created by lenna on 2020/7/29
 * 工作室成员介绍编辑页面
 *
 */
@MakeResult(includeStartForResult = false)
class WorkRoomMemberIntroduceEditActivity : MeiCustomBarActivity() {

    private var mMemberListInfo = arrayListOf<WorkRoomMember>()
    private val mMemberListAdapter by lazy {
        MemberListAdapter(mMemberListInfo).apply {
            setOnItemClickListener { _, _, position ->
                switchTo(position)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_member_introduce_edit)
        title = "成员介绍"
        requestData()
        initView()
        bindAction<Boolean>(CHANG_LOGIN) {
            net_error_layout.apply {
                isVisible = !JohnUser.getSharedUser().hasLogin()
                setBtnText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.try_again) else "立即登录")
                setEmptyText(if (JohnUser.getSharedUser().hasLogin()) Cxt.getStr(R.string.no_network) else "登录后才能编辑")
            }
            if (JohnUser.getSharedUser().hasLogin()) {
                requestData()
            }
        }
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
                finish()
            })
        } else {
            finish()
        }
    }

    private fun checkChanged(): Boolean {
        val introduce = mMemberListInfo.find { it.isChecked }?.introduction
        return introduce ?: "" != introduce_et.text.toString()
    }

    private fun switchTo(position: Int) {
        val introduce = mMemberListInfo.find { it.isChecked }?.introduction
        if (introduce ?: "" != introduce_et.text.toString()) {
            UIToast.toast("请保存当前成员介绍")
        } else {
            mMemberListInfo.filter { it.isChecked }.forEach { it.isChecked = false }
            mMemberListInfo[position].isChecked = true
            mMemberListAdapter.notifyDataSetChanged()
            switchToMember(mMemberListInfo[position])
        }
    }

    /**
     * 被选中的成员
     */
    @SuppressLint("SetTextI18n")
    private fun switchToMember(it: WorkRoomMember?) {
        member_name_tv.text = it?.nickname.orEmpty()
        introduce_et.setText(it?.introduction.orEmpty())
        introduce_et.setSelection(introduce_et.text.length)
        content_length_hint_tv.text = "${it?.introduction?.length ?: 0}/800"
    }

    @SuppressLint("SetTextI18n")
    private fun initView() {
        introduce_et.addTextChangedListener {
            content_length_hint_tv.text = "${it.toString().length}/800"
        }
        net_error_layout.apply {
            setOnBtnClick(View.OnClickListener {
                if (JohnUser.getSharedUser().hasLogin()) {
                    requestData()
                } else {
                    toLogin()
                }
            })
        }
        save_gcv.setOnClickListener {
            saveMemberIntroduce()
        }
        member_rlv.adapter = mMemberListAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.HORIZONTAL
        member_rlv.layoutManager = layoutManager

        this.keyboardBindLife(this) { height ->
            save_gcv.isVisible = height < screenHeight / 4
            if (height < screenHeight / 4) {
                introduce_et.setPadding(introduce_et.paddingLeft, introduce_et.paddingTop, introduce_et.paddingRight, dip(80))
            } else {
                introduce_et.setPadding(introduce_et.paddingLeft, introduce_et.paddingTop, introduce_et.paddingRight, dip(20))
            }

        }

    }

    private fun saveMemberIntroduce() {
        //保存当前被选中的成员
        val memberInfo = mMemberListInfo.find { it.isChecked }
        if (memberInfo != null) {
            showLoading(true)
            apiSpiceMgr.executeKt(EditMemberIntroductionRequest(memberInfo.userId, introduce_et.text.toString()), success = {
                if (it.isSuccess) {
                    UIToast.toast("保存成功")
                    memberInfo.introduction = introduce_et.text.toString()
                } else {
                    UIToast.toast(it.errMsg)
                }
            }, failure = {
                UIToast.toast(it?.currMessage)
            }, finish = { showLoading(false) })
        } else {
            if (mMemberListInfo.isNotEmpty()) {
                switchToFirst()
            } else {
                UIToast.toast("该工作室没有成员，不能保存")
            }
        }
    }

    /**
     * 切换到第一个成员
     */
    private fun switchToFirst() {
        if (mMemberListInfo.isNotEmpty()) {
            mMemberListInfo.filter { it.isChecked }.forEach { it.isChecked = false }
            mMemberListInfo[0].isChecked = true
            mMemberListAdapter.notifyDataSetChanged()
            switchToMember(mMemberListInfo[0])
        }
    }

    /**
     * 请求工作室成员信息
     */
    private fun requestData() {
        showLoading(true)
        apiSpiceMgr.executeKt(WorkRoomMemberInfoRequest(), success = {
            if (it.isSuccess) {
                mMemberListInfo.clear()
                mMemberListInfo.addAll(it.data?.memberList ?: arrayListOf())
                net_error_layout.isVisible = false
                loadMemberInfoData()

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

        }, finish = { showLoading(false) })
    }

    private fun loadMemberInfoData() {
        switchToFirst()
        mMemberListAdapter.notifyDataSetChanged()
    }

}