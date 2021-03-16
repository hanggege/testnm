package com.mei.live.ui

import android.graphics.Color
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.flyco.roundview.RoundTextView
import com.gyf.immersionbar.ImmersionBar
import com.mei.base.ui.nav.Nav
import com.mei.live.LiveEnterType
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.dialog.showSingleBtnCommonDialog
import com.mei.me.ext.filterPattern
import com.mei.orc.ActivityLauncher
import com.mei.orc.ext.addTextChangedListener
import com.mei.orc.ext.selectBy
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.keyboard.hideKeyBoard
import com.mei.orc.util.keyboard.showKeyBoardDelay
import com.mei.player.view.IgnorePlayerBar
import com.mei.wood.R
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.net.MeiUser
import com.net.model.chick.tab.tabbarConfig
import com.net.model.room.ProductCategory.ProductCategoryBean
import com.net.model.user.GroupInfo
import com.net.network.room.ProductCategoryRequest
import com.net.network.room.RoomStartRequest
import com.pili.player
import kotlinx.android.synthetic.main.activity_start_live_room.*
import kotlinx.android.synthetic.main.layout_common_start_live_head.*
import kotlinx.android.synthetic.main.layout_studio_start_live_head.*
import launcher.Boom

/**
 * Created by hang on 2020-02-18.
 * 开启直播界面
 */
const val LIVE_TITLE_PATTERN = "[\\p{Han}\\p{P}A-Za-z0-9]"

class StartLiveRoomActivity : MeiCustomActivity(), IgnorePlayerBar {

    @Boom
    var groupInfo: GroupInfo? = null

    private var mSelectedIndex = -1

    private val mList = arrayListOf<ProductCategoryBean>()
    private val mAdapter by lazy {
        LiveTypeAdapter(mList).apply {
            setOnItemClickListener { _, _, position ->
                mSelectedIndex = position
                notifyDataSetChanged()
                if (groupInfo == null) {
                    if (edit_live_title.text.isNullOrEmpty()) {
                        start_live.alpha = 0.1f
                    } else {
                        start_live.alpha = 1f
                    }
                } else {
                    if (studio_edit_live_title.text.isNullOrEmpty()) {
                        start_live.alpha = 0.1f
                    } else {
                        start_live.alpha = 1f
                    }
                }
            }
        }
    }

    override fun customStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start_live_room)
        ActivityLauncher.bind(this)

        requestCategoryList()
        view_stub.apply {
            layoutResource = if (groupInfo != null) R.layout.layout_studio_start_live_head else R.layout.layout_common_start_live_head
            setOnInflateListener { _, _ ->
                text_select_live_type.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    topToBottom = R.id.view_stub_inflate
                }
                initView()
                initListener()
            }
            inflate()
        }

        ImmersionBar.with(this)
                .statusBarColorInt(Color.parseColor("#303030"))
                .statusBarDarkFont(false)
                .init()
        player.pause()
    }

    private fun requestCategoryList() {
        showLoading(true)
        apiSpiceMgr.executeToastKt(ProductCategoryRequest(), success = {
            if (it.isSuccess) {
                it.data?.list.orEmpty().let { list ->
                    mList.clear()
                    mList.addAll(list)
                    mAdapter.notifyDataSetChanged()
                }
            }
        }, finish = {
            showLoading(false)
        })
    }

    private fun initListener() {
        if (groupInfo != null) {
            studio_back_choice.setOnClickListener { finish() }

            studio_edit_live_title.addTextChangedListener {
                if (it.isEmpty() || mSelectedIndex < 0) {
                    start_live.alpha = 0.1f
                } else {
                    start_live.alpha = 1f
                }
            }
        } else {
            back_choice.setOnClickListener { finish() }

            edit_live_title.addTextChangedListener {
                if (it.isEmpty() || mSelectedIndex < 0) {
                    start_live.alpha = 0.1f
                } else {
                    start_live.alpha = 1f
                }
            }
        }

        start_live.setOnClickListener {
            startLive()
        }

        group_platform_rule.setOnClickListener {
            Nav.toAmanLink(this, tabbarConfig.convention)
        }
    }

    private fun startLive() {
        val title = if (groupInfo == null) edit_live_title.text?.toString().orEmpty() else studio_edit_live_title.text?.toString().orEmpty()
        if (title.isEmpty() || mSelectedIndex == -1) {
            UIToast.toast("请完善对应内容，会获得更多粉丝")
            return
        }
        showLoading(true)
        apiSpiceMgr.executeToastKt(RoomStartRequest(title, mList[mSelectedIndex].pro_cate_id), success = {
            if (it.isSuccess) {
                it.data?.roomInfo?.let { data ->
                    if (data.alertTips.isNullOrEmpty()) {
                        VideoLiveRoomActivityLauncher.startActivity(activity, data.roomId.orEmpty(), LiveEnterType.myself_home_normal_enter, data)
                        finish()
                    } else {
                        showSingleBtnCommonDialog("系统检测到您上场直播异常掉线，当前用户还在直播间内连线，点击确定恢复连线状态", btnText = "确认恢复") { dialog ->
                            dialog.dismissAllowingStateLoss()
                            VideoLiveRoomActivityLauncher.startActivity(activity, data.roomId.orEmpty(), LiveEnterType.myself_home_normal_enter, data)
                            finish()
                        }
                    }
                }
            }
        }, finish = {
            showLoading(false)
        })
    }

    private fun initView() {
        if (groupInfo != null) {
            studio_edit_live_title.filterPattern(LIVE_TITLE_PATTERN, "名字不能包含特殊字符")
            studio_edit_live_title.requestFocus()

            studio_cover.glideDisplay(groupInfo?.avatar.orEmpty(), R.drawable.default_studio_cover)
            studio_name.text = groupInfo?.groupName.orEmpty()
        } else {
            edit_live_title.filterPattern(LIVE_TITLE_PATTERN, "名字不能包含特殊字符")
            edit_live_title.requestFocus()

            anchor_avatar.glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
        }
        showKeyBoardDelay()

        live_type_recycle.layoutManager = GridLayoutManager(this, 2)
        live_type_recycle.adapter = mAdapter
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val currentFocus = currentFocus
            if (currentFocus != null && isShouldHideInput(currentFocus, ev)) {
                currentFocus.hideKeyBoard()
                currentFocus.clearFocus()
            }
            return super.dispatchTouchEvent(ev)
        }
        if (window?.superDispatchTouchEvent(ev) == true) {
            return true
        }
        return onTouchEvent(ev)
    }

    private fun isShouldHideInput(currentFocus: View?, ev: MotionEvent): Boolean {
        if (currentFocus != null && currentFocus is EditText) {
            val leftTop = intArrayOf(0, 0)
            currentFocus.getLocationInWindow(leftTop)
            val left = leftTop[0]
            val top = leftTop[1]
            val right = left + currentFocus.width
            val bottom = top + currentFocus.height
            return !(ev.x > left && ev.x < right && ev.y > top && ev.y < bottom)
        }
        return false
    }

    inner class LiveTypeAdapter(list: MutableList<ProductCategoryBean>) : BaseQuickAdapter<ProductCategoryBean, BaseViewHolder>(R.layout.item_live_type, list) {

        override fun convert(holder: BaseViewHolder, item: ProductCategoryBean) {
            val liveType = holder.getView<RoundTextView>(R.id.live_type)
            liveType.text = item.pro_cate_name
            liveType.delegate.backgroundColor = (mSelectedIndex == holder.layoutPosition).selectBy(Color.WHITE, Color.TRANSPARENT)
            liveType.setTextColor((mSelectedIndex == holder.layoutPosition).selectBy(Color.BLACK, Color.WHITE))
        }

    }
}