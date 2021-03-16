//package com.mei.im.ui
//
//import android.annotation.SuppressLint
//import android.content.Context
//import android.content.Intent
//import android.graphics.Color
//import android.graphics.drawable.ColorDrawable
//import android.os.Build
//import android.os.Bundle
//import android.util.TypedValue
//import android.view.Gravity
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.*
//import androidx.annotation.RequiresApi
//import androidx.core.view.forEach
//import androidx.core.view.isVisible
//import androidx.fragment.app.commitNow
//import com.gyf.immersionbar.ImmersionBar
//import com.joker.im.listener.IMNotifyLevelInterface
//import com.mei.dialog.showSelectedReportReason
//import com.mei.im.checkIMForceOffline
//import com.mei.im.ui.fragment.IMMessageListFragmentLauncher
//import com.mei.im.ui.fragment.showInputState
//import com.mei.live.manager.genderAvatar
//import com.mei.live.ui.dialog.showSendGiftDialog
//import com.mei.live.ui.ext.LevelSize
//import com.mei.live.ui.ext.getBackGroundLevelResource
//import com.mei.live.views.followFriend
//import com.mei.me.ext.refreshFollowState
//import com.mei.orc.ActivityLauncher
//import com.mei.orc.Cxt
//import com.mei.orc.ext.dip
//import com.mei.orc.ext.layoutInflaterKt
//import com.mei.orc.ext.selectBy
//import com.mei.orc.ext.sp
//import com.mei.orc.imageload.glide.glideDisplay
//import com.mei.orc.john.model.JohnUser
//import com.mei.orc.ui.toast.UIToast
//import com.mei.widget.actionbar.defaultRightView
//import com.mei.wood.R
//import com.mei.wood.cache.requestUserInfo
//import com.mei.wood.ext.AmanLink
//import com.mei.wood.extensions.executeKt
//import com.mei.wood.extensions.executeToastKt
//import com.mei.wood.ui.MeiCustomActivity
//import com.mei.wood.util.MeiUtil
//import com.net.MeiUser
//import com.net.model.chick.report.ChatC2CPubInfo
//import com.net.model.chick.report.ChatC2CUseInfo
//import com.net.model.chick.report.ReportBean
//import com.net.model.rose.RoseFromSceneEnum
//import com.net.network.chick.report.PublisherInfoRequest
//import com.net.network.chick.report.ReportRequest
//import com.net.network.chick.report.UserInfoRequest
//import com.zhy.view.flowlayout.FlowLayout
//import com.zhy.view.flowlayout.TagAdapter
//import kotlinx.android.synthetic.main.chat_c2c_activity.*
//import kotlinx.android.synthetic.main.chat_c2c_im_audience.*
//import kotlinx.android.synthetic.main.chat_c2c_im_audience.user_extra_tv
//import kotlinx.android.synthetic.main.chat_c2c_im_live.*
//import launcher.Boom
//
///**
// * 佛祖保佑         永无BUG
// *
// * @author Created by joker on 2020/4/20
// */
//class C2CIM : MeiCustomActivity(), IMNotifyLevelInterface {
//    @Boom
//    var chatId: String = ""
//    val reportPopupWindow by lazy {
//        val contentView = layoutInflaterKt(R.layout.c2c_report_popu_layout)
//        val popupWindow = PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
//            isOutsideTouchable = true
//            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
//            window.attributes = window.attributes.apply { alpha = 0.4f }
//            setOnDismissListener { window.attributes = window.attributes.apply { alpha = 1f } }
//        }
//        contentView.setOnClickListener {
//            popupWindow.dismiss()
//            showSelectedReportReason {
//                apiSpiceMgr.executeToastKt(ReportRequest(ReportBean().apply {
//                    reasonId = it.type
//                    reportUser = chatId.toIntOrNull() ?: 0
//                }), success = {
//                    if (it.isSuccess) UIToast.toast(this, "举报成功")
//                })
//            }
//        }
//        popupWindow
//    }
//    val imMsgListFragment by lazy { IMMessageListFragmentLauncher.newInstance(chatId) }
//    val lableAdapter by lazy {
//        TagLableAdapter(this, lableList)
//    }
//    val lableList = arrayListOf<String>()
//    val rightText by lazy {
//        TextView(this@IMC2CMessageActivity).apply {
//            layoutParams = LinearLayout.LayoutParams(dip(52), dip(26)).apply {
//                gravity=Gravity.CENTER
//            }
//            gravity=Gravity.CENTER
//            text = ""
//            textSize= sp(5).toFloat()
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                setTextColor(getColor(R.color.color_333333))
//            }
//            background = resources.getDrawable(R.drawable.bg_round_1dp_ffe03)
//
//        }
//    }
//    override fun customStatusBar(): Boolean = true
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        readChatId(intent)
//        setContentView(R.layout.chat_c2c_activity)
//        initView()
//
//        supportFragmentManager.commitNow(allowStateLoss = true) { replace(R.id.im_msg_list_fragment, imMsgListFragment) }
//        checkIMForceOffline {
//            UIToast.toast(this, "聊天被踢，请重新登录")
//            finish()
//        }
//        apiSpiceMgr.requestUserInfo(arrayOf(chatId.toIntOrNull() ?: 0), true) { list ->
//            val info = list.firstOrNull()
//            title = info?.nickname.orEmpty()
//            mei_action_bar.centerContainer.forEach { (it as? TextView)?.text = info?.nickname.orEmpty() }
//            info?.isPublisher?.let { publisherInfo(chatId, it) }
//            if (info?.isPublisher == MeiUser.getSharedUser().info?.isPublisher) {
//                UIToast.toast(this, if (info?.isPublisher == true) "暂不支持知心达人之间的私聊功能" else "暂不支持用户之间的私聊功能")
//                finish()
//            }
//
//        }
//
//
//    }
//
//    /**
//     * 主播信息介绍
//     */
//    fun initPublisher(info: ChatC2CPubInfo.InfoBean) {
//        personal_avatar.glideDisplay(info?.userInfo.avatar.orEmpty(), R.drawable.default_avatar_50, info?.userInfo.gender.genderAvatar())
//        info.publisherTags.forEach {
//            lableList.add(it)
//        }
//        id_flowlayout_zhubo?.adapter = lableAdapter
//        tv_user_jieshao?.text = info?.introduction
//        personal_live_status.isVisible=(2 == info.onlineStatus)
//        rightText.isVisible=(!info?.isIsFollow)
//        rightText.text = (info?.isIsFollow).selectBy("已关注", "关注")
//        rightText.setOnClickListener {
//            followFriend(info.userId, 0,info.userId.toString()) { isOk ->
//                if (isOk) {
//                    UIToast.toast("关注成功")
//                    rightText.isVisible=false
//                }
//            }
//        }
//    }
//
//    /**
//     * 非主播信息介绍
//     */
//    @SuppressLint("SetTextI18n")
//    fun initNoPublisher(info: ChatC2CUseInfo.InfoBean) {
//        fl_head_audience.glideDisplay(info.avatar.orEmpty(), R.drawable.default_avatar_50, info.gender.genderAvatar())
//        tv_level.text = info.userLevel.toString()
//        tv_level.setBackgroundResource(getBackGroundLevelResource(info.userLevel, LevelSize.Normal))
//        if (info.gender == 1) {
//            user_extra_tv.text = "男 ${info.birthYear.orEmpty()}"
//            user_extra_tv.setBackColor(Color.parseColor("#6995ff"))
//        } else {
//            user_extra_tv.text = "女 ${info.birthYear.orEmpty()}"
//            user_extra_tv.setBackColor(Color.parseColor("#FF69B0"))
//        }
//        info.interests.forEach {
//            lableList.add(it.name)
//        }
//        id_flowlayout_no_zhubo?.adapter = lableAdapter
//        tv_xin_coin.text = info.sendCoinCount.toString()
//        tv_give_me_coin.text = info.sendMeCount.toString()
//        tv_give_me_conet.text = info.upstreamCount.toString()
//    }
//
//    override fun onNewIntent(intent: Intent?) {
//        super.onNewIntent(intent)
//        intent?.apply {
//            /** 进行了新对话，销毁当前，重新创建一个对话 **/
//            finish()
//            startActivity(intent)
//        }
//    }
//
//    /**
//     * 查询用户信息
//     */
//    private fun publisherInfo(targerID: String, isPub: Boolean) {
//        if (isPub) {
//            apiSpiceMgr.executeKt(PublisherInfoRequest(targerID), success = {
//                initPublisher(it.data.info)
//            })
//        } else {
//            rightText.visibility=View.GONE
//            apiSpiceMgr.executeKt(UserInfoRequest(targerID), success = {
//                it?.let { initNoPublisher(it.data.info) }
//            })
//        }
//    }
//
//    private fun initView() {
//        ImmersionBar.with(this).apply {
//            statusBarColorInt(Color.WHITE)
//            statusBarDarkFont(true)
//            fitsSystemWindows(true)
//            supportActionBar(showActionBar())
//                    .keyboardEnable(true)
//                    .setOnKeyboardListener { isPopup, keyboardHeight ->
//                        if (isPopup) imMsgListFragment.showInputState(1)
//                    }
//        }.init()
//        im_user_option_layout.isVisible = MeiUser.getSharedUser().info?.isPublisher == false
//        c2c_im_live.isVisible = MeiUser.getSharedUser().info?.isPublisher == false
//        c2c_im_audience.isVisible = MeiUser.getSharedUser().info?.isPublisher == true
//
//        mei_action_bar.apply {
//            bottomLine.isVisible = false
//            rightContainer.addView(LinearLayout(this@IMC2CMessageActivity).apply {
//                layoutParams = RelativeLayout.LayoutParams(
//                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 100f, resources.displayMetrics).toInt(),
//                        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics).toInt()
//                )
//                orientation=LinearLayout.HORIZONTAL
//                addView(rightText)
//                addView(defaultRightView().apply {
//                    setOnClickListener {
//                        reportPopupWindow.showAsDropDown(it, -dip(50), dip(5))
//                        window.attributes = window.attributes.apply { alpha = 0.4f }
//                    }
//                })
//            })
//////            rightContainer.addView(defaultRightView())0
////            rightContainer.setOnClickListener {
////                reportPopupWindow.showAsDropDown(it, -dip(50), dip(5))
////                window.attributes = window.attributes.apply { alpha = 0.4f }
////            }
//            leftContainer.removeAllViews()
//            leftContainer.addView(ImageView(context).apply {
//                setPadding(dip(20), dip(16), dip(20), dip(16))
//                setImageResource(R.drawable.bg_black_back_arrow)
//            })
//            leftContainer.setOnClickListener { finish() }
//        }
//
//        apply_private_live_layout.setOnClickListener { UIToast.toast(this, "私密连线，可以调用IMMessageListFragment中的私密连线方法") }
//
//        send_private_gift_layout.setOnClickListener {
//            //            imMsgListFragment.setGiftBannerVisible(true)
//            apiSpiceMgr.requestUserInfo(arrayOf(chatId.toIntOrNull() ?: 0)) { list ->
//                val info = list.firstOrNull()
//                showSendGiftDialog(chatId.toInt(), info?.nickname.orEmpty(), "", true, roseFromScene = RoseFromSceneEnum.EXCLUSIVE_CHAT)
//            }
//        }
//
//    }
//
//
//
//    private fun readChatId(intent: Intent) {
//        val linkUrl = intent.dataString.orEmpty()
//        if (linkUrl.isNotEmpty()) {
//            /** 读取内链 **/
//            chatId = MeiUtil.getOneID(linkUrl, AmanLink.URL.USER_CHAT_PATTERN)
//        } else {
//            ActivityLauncher.bind(this)
//        }
//        if (chatId == "") {
//            UIToast.toast(this, "未找到该用户")
//            finish()
//        } else if (chatId == JohnUser.getSharedUser().userIDAsString) {
//            UIToast.toast(this, "不能和自己聊天哦")
//            finish()
//        }
//    }
//
//    override fun isAllIgnore(): Boolean = false
//
//    override fun IgnoreByID(): String = chatId
//
//    override fun onBackPressed() {
//        if (imMsgListFragment.upstreamLoadingView?.isShow == true) {
//            imMsgListFragment.upstreamLoadingView?.hideAnim()
//        } else {
//            super.onBackPressed()
//        }
//    }
//
//    class TagLableAdapter(var context: Context, datas: ArrayList<String>) : TagAdapter<String>(datas) {
//        override fun getView(parent: FlowLayout?, position: Int, item: String?): View {
//            val iv: RelativeLayout = LayoutInflater.from(context).inflate(R.layout.chat_c2c_lable_item, parent, false) as RelativeLayout
//            val txtItem: TextView = iv.findViewById(R.id.c2c_chat_info_text_label)
//            txtItem.text = item
//            return iv
//        }
//    }
//}