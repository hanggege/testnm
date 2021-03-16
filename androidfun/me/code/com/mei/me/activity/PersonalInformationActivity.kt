package com.mei.me.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.view.updatePadding
import com.joker.TdType
import com.joker.im.custom.chick.InviteJoinInfo
import com.joker.model.BackResult
import com.joker.model.ErrResult
import com.mei.base.common.CHANG_LOGIN
import com.mei.base.util.td.parseState
import com.mei.dialog.showSelectAgeDialog
import com.mei.dialog.showSelectSexDialog
import com.mei.live.manager.genderAvatar
import com.mei.me.ext.*
import com.mei.me.utils.BindBack
import com.mei.me.utils.BindManager
import com.mei.orc.ActivityLauncher
import com.mei.orc.Cxt
import com.mei.orc.event.bindAction
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIntent
import com.mei.orc.ext.selectBy
import com.mei.orc.http.listener.PureRequestListener
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.orc.util.keyboard.hideKeyBoardForce
import com.mei.orc.util.startactivity.startFragmentForResult
import com.mei.provider.ProjectExt
import com.mei.widget.menupoint.MenuPointView
import com.mei.wood.R
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialogLauncher
import com.mei.wood.event.ISystemInviteJoinShow
import com.mei.wood.extensions.executeKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.chick.user.ChickUserInfo
import com.net.network.chick.login.OauthConnectRequest
import com.net.network.chick.user.GetNicknameRequest
import com.net.network.chick.user.MyInfoRequest
import com.net.network.chick.user.WeChatUnbindRequest
import kotlinx.android.synthetic.main.activity_personal_information.*
import kotlinx.android.synthetic.main.activity_personal_information.view.*
import kotlinx.android.synthetic.main.item_edit_layout.view.*
import launcher.MakeResult
import com.mei.me.activity.RESULT_EIDT_INFO as RESULT_EIDT_INFO1


/**
 * Created by hang on 2019-12-03.
 * 完善个人资料界面
 */
const val CHINESE_PATTERN_COPY = "[\u4e00-\u9fa5]+"
var lastText = ""
val lableCount: String = Cxt.getStr(R.string.personal_lable_txt)

@MakeResult(includeStartForResult = false)
class PersonalInformationActivity : MeiCustomBarActivity(), ISystemInviteJoinShow, BindBack {
    private var mIvRightOptions: MenuPointView? = null
    private var mUserInfo: ChickUserInfo = MeiUser.getSharedUser()
    private var isWechatBind: Boolean = false
    private var isPhoneBind: Boolean = false
    var isTrainer: Boolean = false
    var interestedMap = HashMap<Int, String>()
    var skillMap = HashMap<Int, String>()
    var mGenderType: Int = -1

    private val bindManager: BindManager by lazy {
        BindManager(this, apiSpiceMgr)
    }
    var labelList = arrayListOf<String>()
    val mTagAdapter by lazy {
        LabelAdapter(this, labelList, personal_lable_count, tv_personal_infomation_add_lable)
    }

    private var ageRangeList = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_information)
        ActivityLauncher.bind(this)
        title = "我的资料"
        showBottomLine(false)
        id_flowlayout.adapter = mTagAdapter
        initListener()
        requestData()
        showLoadingCover()
        mIvRightOptions = MenuPointView(this)
        mIvRightOptions?.let {
            setCustomView(it, 1)
            it.pointNumber = 3
            it.pointWidth = dip(4).toFloat()
            it.setPointColor(Cxt.getColor(R.color.color_262626))
            it.direction = 0
            it.updateLayoutParams<ViewGroup.LayoutParams> {
                width = dip(50)
                height = dip(50)
            }
            it.updatePadding(left = dip(15), right = dip(15), bottom = dip(16), top = dip(16))
            it.setOnClickListener { _ ->
                window.attributes = window.attributes.apply { alpha = 0.4f }
                showCancelAccountPopWindow(it)
            }
            it.isVisible = MeiUser.getSharedUser().info?.isPublisher != true
        }


    }

    override fun onRestart() {
        super.onRestart()
        nick_name.postDelayed({
            nick_name.hideKeyBoardForce()
        }, 300)

    }

    fun requestData() {
        apiSpiceMgr.executeKt(MyInfoRequest(), {
            if (it.isSuccess) {
                it.data?.let { data ->
                    MeiUser.setUserInfo(data)
                    updateUI(data)
                    ageRangeList.clear()
                    ageRangeList.addAll(data.years)
                }
            } else {
                UIToast.toast(it.errMsg)

            }
        }, failure = {
            UIToast.toast(it?.currMessage)
        }, finish = {
            showLoading(false)
        })
    }

    @SuppressLint("NewApi")
    private fun updateUI(userInfo: ChickUserInfo) {
        //判断是否是主播隐藏个人简介和标签
        ly_lable_info.isVisible = userInfo.info?.isPublisher == true
        mUserInfo = userInfo
        isTrainer = userInfo.info?.isPublisher ?: false

        //头像
        image_avatar.glideDisplay(userInfo.info?.avatar.orEmpty(), userInfo.info?.gender.genderAvatar(userInfo.info?.isPublisher))
        //性别
        sex_item.editText = (userInfo.info?.gender == 1).selectBy("男", "女")
        mGenderType = userInfo.info?.gender ?: -1
        //年龄
        if (userInfo.info?.birthYear.orEmpty().isNotEmpty()) {
            age_item.editText = userInfo.info?.birthYear
        }
        //擅长领域
        skill_item.apply {
            userInfo.info?.publisherSkills.orEmpty().joinToString(",") { it.name }.let {
                if (it.isNotEmpty()) {
                    editHint = ""
                    this@PersonalInformationActivity.skill_text.text = it
                }
            }


        }
        skill_layout.apply {
            userInfo.info?.publisherSkills?.forEach {
                if (null != it) {
                    skillMap[it.id] = it.name.orEmpty()
                }
            }
            setOnClickListener {
                startFragmentForResult(Intent(this@PersonalInformationActivity, InterestSelectActivity::class.java).also {
                    it.putExtra("sel", skillMap)
                }) { _, data ->
                    /**跳转页面有可能会返回空数据，所以需要做一下空处理*/
                    @Suppress("UNCHECKED_CAST")
                    val dataMap = data?.getSerializableExtra(INTEREST_INFO_KEY) as? HashMap<Int, String>
                            ?: HashMap()
                    if (dataMap.isNotEmpty()) {
                        skillMap = dataMap
                        skillMap.values.joinToString(",").let {
                            if (it.isNotEmpty()) {
                                skill_item.editHint = ""
                                skill_text.text = it
                            }
                        }
                    }
                }
            }
            isVisible = userInfo.info?.isPublisher ?: false
        }
        //感兴趣内容
        interested_item.apply {
            userInfo.info?.interests.orEmpty().joinToString(",") { it.name }.let {
                if (it.isNotEmpty()) {
                    editHint = ""
                    this@PersonalInformationActivity.interested_text.text = it
                }
            }


        }
        interested_layout.apply {
            userInfo.info?.interests?.forEach {
                interestedMap[it.id] = it.name
            }
            setOnClickListener {
                startFragmentForResult(Intent(this@PersonalInformationActivity, InterestSelectActivity::class.java).also {
                    it.putExtra("sel", interestedMap)
                }) { _, data ->
                    /**跳转页面有可能会返回空数据，所以需要做一下空处理*/
                    @Suppress("UNCHECKED_CAST")
                    val dataMap = data?.getSerializableExtra(INTEREST_INFO_KEY) as? HashMap<Int, String>
                            ?: HashMap()
                    if (dataMap.isNotEmpty()) {
                        interestedMap = dataMap
                        interestedMap.values.joinToString(",").let {
                            if (it.isNotEmpty()) {
                                interested_item.editHint = ""
                                interested_text.text = it
                            }
                        }
                    }

                }
            }
            isGone = userInfo.info?.isPublisher ?: false
        }
        //个人简介
        personal_introduction_et_input.apply {
            content = userInfo.info?.introduction

        }

        //个人标签
        userInfo.info?.tags.let {
            labelList.addAll(it.orEmpty())
            mTagAdapter.notifyDataChanged()
            tv_personal_infomation_add_lable.isVisible = labelList.size < 10
            personal_lable_count.text = String.format(lableCount, (10 - labelList.size))
        }
        //是否绑定手机号
        bind_phone_item.apply {
            userInfo.extra?.bindPhone?.let {
                userInfo.info?.phone?.let { it1 -> refreshPhoneState(it, it1) }
                setOnClickListener {
                    if (isPhoneBind) {
                        //显示提示框
                        NormalDialogLauncher.newInstance().showDialog(this@PersonalInformationActivity, DialogData(message = "当前手机号为${userInfo.info?.phone}", title = "是否更换绑定手机号", okStr = "更换"), okBack = {
                            gotoBind()
                        })

                    } else {
                        gotoBind()
                    }

                }

            }
        }


        //是否绑定微信
        bind_wechat_item.apply {
            userInfo.extra?.bindWechat?.let {
                refreshWechatState(it)
                setOnClickListener {
                    if (isWechatBind) {
                        if (!isPhoneBind) {
                            UIToast.toast("请先绑定手机号")
                            return@setOnClickListener
                        }
                        //显示提示框
                        NormalDialogLauncher.newInstance().showDialog(this@PersonalInformationActivity, "是否确定解绑微信", okBack = {
                            //解绑微信
                            apiSpiceMgr.executeKt(WeChatUnbindRequest(ProjectExt.WEIXIN_ANDROID), success = {
                                //                                refreshWechatState(false)
                                requestData()
                            })
                        })
                    } else {
                        //跳转至微信授权绑定
                        bindManager.performBind(activity, TdType.weixin)
                    }

                }
            }
        }
        //昵称
        nick_name.apply {
            editText = userInfo.info?.nickname.orEmpty()
            edit_text.isEnabled = userInfo.info?.isPublisher == false
            if (userInfo.info?.isPublisher == true) {
                setOnClickListener {
                    UIToast.toast("修改昵称请联系专员后台操作")
                }
            } else {
                setOnClickListener {
                    nick_name.requestEditFocus()
                }
            }
        }
        val showLabel = userInfo.info?.isPublisher?.not() ?: false
        icon_edit_tip.isGone = showLabel
        label_change_nickname.isVisible = showLabel
        if (showLabel) {
            label_change_nickname.setOnClickListener {
                getRandomNickname()
            }
        }
    }

    private fun getRandomNickname() {
        apiSpiceMgr.executeKt(GetNicknameRequest(), success = {
            if (it.isSuccess && !it.data.value.isNullOrEmpty()) {
                nick_name.editText = it.data.value
            }
        })
    }

    private fun gotoBind() {
        startFragmentForResult(getIntent<BindPhoneActivity>(), 50) { code, _ ->
            if (code == 50) {
                requestData()
            }
        }
    }

    private fun refreshWechatState(isBind: Boolean) {
        bind_wechat.editText = isBind.selectBy("已绑定", "未绑定")
        bind_wechat_tip.isGone = mUserInfo.extra?.bindTask ?: false || isBind
        isWechatBind = isBind
    }

    private fun refreshPhoneState(isBind: Boolean, phone: String) {
        var fphone = phone
        if (!phone.contains("*") && phone.length == 11) {
            fphone = phone.substring(0, 3) + "****" + phone.substring(7, phone.length)
        }

        bind_phone.editText = isBind.selectBy(fphone, "未绑定")
        bind_phone_tip.isGone = mUserInfo.extra?.bindTask ?: false || isBind
        isPhoneBind = isBind
    }

    @SuppressLint("NewApi")
    private fun initListener() {
        bindAction<Boolean>(CHANG_LOGIN) {
            if (it != true) {
                //注销或者是退出登录
                finish()
            }
        }
        hideKeyBoard()
        //昵称
        nick_name.setOnClickListener {
            nick_name.requestEditFocus()
        }
        //头像
        head_layout.setOnClickListener {
            uploadHeader()
        }
        //年龄
        age_item.setOnClickListener {
            showSelectAgeDialog(ageRangeList, age_item.editText.toString()) { _, age ->
                age_item.editText = age.toString()
            }
        }
        //性别
        sex_item.setOnClickListener {
            showSelectSexDialog { genderType, sex ->
                mGenderType = genderType
                sex_item.editText = sex.toString()
            }
        }
        save_information.setOnClickListener {
            requestSaveData()
        }


        findViewById<TextView>(R.id.tv_personal_infomation_add_lable).apply {
            setOnClickListener {
                //添加标签
                showAddLabelDialog()

            }
        }
        findViewById<TextView>(R.id.tv_personal_edit_info).setOnClickListener {
            //个人简介编辑
            activity.startFragmentForResult(EditIntroductionActivityLauncher.getIntentFrom(this, personal_introduction_et_input.content.orEmpty()), 100) { requestCode, data ->
                if (requestCode == 100) {
                    personal_introduction_et_input.content = data?.getStringExtra(RESULT_EIDT_INFO1).orEmpty()
                }
            }
        }


    }

    override fun onClickLeft(view: View) {
        onBackPressed()
    }

    /**
     * 绑定微信失败回调
     */
    override fun bindFailure(result: ErrResult) {
        UIToast.toast(activity, result.msg.orEmpty().ifEmpty { "绑定失败" })
    }

    /**
     * 绑定微信成功回调，成功时就已经实时将服务器数据更改了
     */
    override fun bindSuccess(success: BackResult) {
        val request = OauthConnectRequest(success.type.parseState(), success.code, "1")
        showLoading(true)
        apiSpiceMgr.executeKt(activity, request, success = {
            MeiUser.resetUser(apiSpiceMgr, object : PureRequestListener<ChickUserInfo.Response>() {
                override fun onResponseCorrect(result: ChickUserInfo.Response?) {
                    try {
                        if (result?.data != null) {
                            requestData()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

            })
        }, finish = {
            showLoading(false)
        })
    }


    override fun onBackPressed() {
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

    override fun isShow(sendId: String, info: InviteJoinInfo): Boolean = false


}




