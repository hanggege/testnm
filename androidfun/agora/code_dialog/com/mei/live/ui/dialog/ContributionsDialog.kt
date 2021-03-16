package com.mei.live.ui.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.joker.im.bindEventLifecycle
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.joker.im.listener.IMAllEventManager
import com.joker.im.mapToMeiMessage
import com.joker.im.message.CustomMessage
import com.mei.base.network.holder.SpiceHolder
import com.mei.live.manager.genderAvatar
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.adapter.ContributionAdapter
import com.mei.live.ui.ext.LevelSize
import com.mei.live.ui.ext.formatContribute
import com.mei.live.ui.ext.getBackGroundLevelResource
import com.mei.live.ui.requestGiftInfo
import com.mei.orc.Cxt
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.orc.ext.dip
import com.mei.orc.ext.getIndexOrNull
import com.mei.orc.imageload.glide.glideDisplay
import com.mei.orc.john.model.JohnUser
import com.mei.wood.R
import com.mei.wood.cache.requestUserInfo
import com.mei.wood.dialog.DialogData
import com.mei.wood.dialog.NormalDialog
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.MeiUser
import com.net.model.room.ContributionsInfo
import com.net.model.room.RoomType
import com.net.model.rose.RoseFromSceneEnum
import com.net.network.room.ContributeListRequest
import com.tencent.imsdk.TIMMessage
import kotlinx.android.synthetic.main.dialog_contribute_list.*
import kotlinx.android.synthetic.main.view_contribute_avatar_rank.*
import kotlinx.android.synthetic.main.view_contribute_head.*
import kotlinx.android.synthetic.main.view_contribute_list_head.*


/**
 *  Created by zhanyinghui on 2020/4/9
 *  Des:
 */


fun FragmentActivity.showContributeDialog(publishId: String) {
    //TODO getData()
    ContributionsDialog().apply {
        this.arguments = Bundle().apply {
            putString("PublishId", publishId)
        }
        this.show(supportFragmentManager, "ContributionsDialog")
    }
}


class ContributionsDialog : BottomDialogFragment() {
    private val data = ArrayList<ContributionsInfo.ContributionBean>()
    private var nextPageNo: Int = 0
    private var hasMore = false
    private val REFRESH_TYPE_TODAY = 0x001
    private val REFRESH_TYPE_TOTAL = 0x002
    private var currentType = REFRESH_TYPE_TOTAL

    val request: ContributeListRequest by lazy {
        ContributeListRequest().apply {
            publisherId = requireArguments().getString("PublishId")
            isDaily = true
            pageNo = 1
        }
    }

    private val dialog by lazy {
        val questionDialog = NormalDialog()
        questionDialog
    }
    private val questionView by lazy {
        val view: View = LayoutInflater.from(context).inflate(R.layout.dialog_contribute_question, null)
        view.setOnClickListener {
            dialog.dismiss()
        }
        val cancelView: View = view.findViewById(R.id.cancel)
        cancelView.setOnClickListener {
            dialog.dismiss()
        }
        view
    }
    private val adapter by lazy {
        ContributionAdapter(R.layout.view_contributes_item, data).apply {
            setEmptyView(LayoutInflater.from(activity).inflate(R.layout.view_contribute_list_empty, null))
            setOnItemClickListener { _, _, position ->
                val item = this.getItemOrNull(position)
                (activity as? VideoLiveRoomActivity)?.apply {
                    showUserInfoDialog(roomInfo, item?.userInfo?.userId ?: 0) { type, info ->
                        if (type == 0) {
                            apiSpiceMgr.requestUserInfo(arrayOf(info.userId), true) { list ->
                                list.firstOrNull()?.let { info ->
                                    showInputKey(info)
                                }
                            }
                        }
                    }
                }
            }
        }

    }
    val checkNewEvent by lazy {
        object : IMAllEventManager() {
            override fun onNewMessages(msgs: MutableList<TIMMessage>?): Boolean {
                val customList = msgs.orEmpty()
                        .mapNotNull { it.mapToMeiMessage() as? CustomMessage }
                        .filter { !it.isDeleted() && it.customMsgType == CustomType.send_gift }//去掉已删除的消息
                        .mapNotNull { it.customInfo?.data as? ChickCustomData }
                        .filter { it.userId == JohnUser.getSharedUser().userID }
                if (customList.isNotEmpty()) {
                    requestGiftInfo {
                        refresh(request.isDaily)
                    }
                }

                return super.onNewMessages(msgs)
            }
        }
    }

    private val dialogData by lazy {
        val dialogData = DialogData(customView = questionView, key = "ContributeDialog", backCanceled = false)
        dialogData
    }

    override fun onSetInflaterLayout(): Int {
        return R.layout.dialog_contribute_list
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        refresh(false)
        checkNewEvent.bindEventLifecycle(this)
    }

    fun initView() {
        bottom_layout.isVisible = request.publisherId != JohnUser.getSharedUser().userIDAsString
        recycler_view.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recycler_view.adapter = adapter
        today_contribute.setOnClickListener {
            setSelected(REFRESH_TYPE_TODAY)
            refresh(true)
        }
        total_contribute.setOnClickListener {
            setSelected(REFRESH_TYPE_TOTAL)
            refresh(false)
        }
        contribute_question.setOnClickListener {
            showQuestionDialog()
        }
        adapter.loadMoreModule.setOnLoadMoreListener {
            if (hasMore) {
                loadMore(nextPageNo, currentType)
            }
        }
        send_gift.setOnClickListener {
            /** 拉起送礼面板 **/
            if (activity is VideoLiveRoomActivity) {
                with(activity as VideoLiveRoomActivity) {
                    apiSpiceMgr.requestUserInfo(arrayOf(roomInfo.createUser?.userId ?: 0)) { data ->
                        val user = data.firstOrNull()
                        user?.let {
                            showSendGiftDialog(it.userId, it.nickname.orEmpty(), roomInfo.roomId, roseFromScene =
                            if (roomInfo.roomType == RoomType.EXCLUSIVE) RoseFromSceneEnum.EXCLUSIVE_ROOM else RoseFromSceneEnum.COMMON_ROOM,
                                    fromType = "榜单")
                        }
                    }
                }
            }
        }
        setSelected(REFRESH_TYPE_TOTAL)
        root_view.setOnClickListener {
            dismiss()
        }
    }

    private fun showQuestionDialog() {
        if (dialogData.customView != null) {
            if (dialogData.customView!!.parent != null) {
                val parent = dialogData.customView!!.parent as ViewGroup
                parent.removeView(dialogData.customView)
            }
        }
        dialog.showDialog(context as FragmentActivity, dialogData, {}, {})
    }


    fun updateView(data: ContributionsInfo, isRefresh: Boolean, type: Int) {
        if (currentType == type) {
            nextPageNo = data.rank.nextPageNo
            hasMore = data.rank.hasMore
            adapter.loadMoreModule.loadMoreComplete()
            adapter.loadMoreModule.isEnableLoadMore = hasMore
            if (isRefresh) {
                //更新顶部
                updateRankView(data)
                //更新底部
                updateMeRankView(data)
                if (data.rank.list.size > 3) {
                    recycle_view_head.visibility = View.VISIBLE
                    adapter.refresh(data.rank.list.drop(3))
                } else {
                    recycle_view_head.visibility = View.GONE
                }

            } else {
                adapter.addAll(data.rank.list)
            }
        }

    }

    /***
     * 更新前三名的视图
     */
    @SuppressLint("SetTextI18n")
    @Suppress("DEPRECATION")
    fun updateRankView(data: ContributionsInfo) {
        val list = data.rank.list


        if ((data.coinCount == 0.toLong())) {
            title_containers.visibility = View.GONE
        } else {
            title_containers.visibility = View.VISIBLE
            total_contribute_text.paint.isFakeBoldText = true
            total_contribute_coin.paint.isFakeBoldText = true
            if (currentType == REFRESH_TYPE_TODAY) {
                total_contribute_text.text = "今日互动"
            } else {
                total_contribute_text.text = "累计互动"
            }
            total_contribute_coin.text = formatContribute(data.coinCount, "w")

        }
        /**
         * 总贡献榜最底部
         */
        if (list.size > 0) {
            val info = list.getIndexOrNull(0)?.userInfo
            image_avatar_first.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
            image_avatar_first.setOnClickListener { }
            list.getIndexOrNull(0)?.userInfo?.let {
                image_avatar_first.bindClick(it.userId)
            }
            soft_first_view.visibility = View.GONE
            contribute_name_first.setTextColor(ContextCompat.getColor(Cxt.get(), R.color.color_333333))
            contribute_name_first.text = list[0].userInfo.nickname
            contribute_name_first.paint.isFakeBoldText = true
            if (list[0].userInfo.nickname.length > 5) {
                contribute_name_first.text = list[0].userInfo.nickname.substring(0, 4) + "..."
            } else {
                contribute_name_first.text = list[0].userInfo.nickname
            }
            val contributeNumber = "<font color = \"#FF4F00\">" + formatContribute(list.first().coinCount, "w") + "</font>"
            contribute_coin_first.text = Html.fromHtml(Cxt.getStr(R.string.contribute_coin_mark, contributeNumber))
            if (list[0].userInfo.userLevel > 0) {
                level_container_first.visibility = View.VISIBLE
                if (list[0].userInfo.userLevel > 9) {
                    level_first.setPadding(0, 0, dip(4), 0)
                } else {
                    level_first.setPadding(0, 0, dip(9), 0)
                }
                level_image_first.setImageResource(getBackGroundLevelResource(list[0].userInfo.userLevel, LevelSize.Normal))
                level_first.text = list[0].userInfo.userLevel.toString()
            } else {
                level_container_first.visibility = View.GONE
            }

        }

        if (list.size > 1) {
            val info = list.getIndexOrNull(1)?.userInfo
            image_avatar_second.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
            image_avatar_second.setOnClickListener { }
            list.getIndexOrNull(1)?.userInfo?.let {
                image_avatar_second.bindClick(it.userId)
            }
            soft_second_view.visibility = View.GONE
            contribute_name_second.paint.isFakeBoldText = true
            contribute_name_second.setTextColor(Cxt.getColor(R.color.color_333333))
            if (list[1].userInfo.nickname.length > 5) {
                contribute_name_second.text = list[1].userInfo.nickname.substring(0, 4) + "..."
            } else {
                contribute_name_second.text = list[1].userInfo.nickname
            }
            val contributeNumber = "<font color = \"#FF4F00\">" + formatContribute(list[1].coinCount, "w") + "</font>"
            contribute_coin_second.text = Html.fromHtml(Cxt.getStr(R.string.contribute_coin_mark, contributeNumber))
            if (list[1].userInfo.userLevel > 0) {
                level_container_second.visibility = View.VISIBLE
                level_image_second.setImageResource(getBackGroundLevelResource(list[1].userInfo.userLevel, LevelSize.Normal))
                if (list[1].userInfo.userLevel > 9) {
                    level_second.setPadding(0, 0, dip(4), 0)
                } else {
                    level_second.setPadding(0, 0, dip(9), 0)
                }
                level_second.text = list[1].userInfo.userLevel.toString()

            } else {
                level_container_second.visibility = View.GONE
            }
        }
        if (list.size > 2) {
            val info = list.getIndexOrNull(2)?.userInfo
            image_avatar_third.glideDisplay(info?.avatar.orEmpty(), info?.gender.genderAvatar(info?.isPublisher))
            image_avatar_third.setOnClickListener { }
            list.getIndexOrNull(2)?.userInfo?.let {
                image_avatar_third.bindClick(it.userId)
            }
            soft_third_view.visibility = View.GONE
            contribute_name_third.paint.isFakeBoldText = true
            contribute_name_third.setTextColor(resources.getColor(R.color.color_333333))
            if (list[2].userInfo.nickname.length > 5) {
                contribute_name_third.text = list[2].userInfo.nickname.substring(0, 4) + "..."
            } else {
                contribute_name_third.text = list[2].userInfo.nickname
            }
            val contributeNumber = "<font color = \"#FF4F00\">" + formatContribute(list[2].coinCount, "w") + "</font>"
            contribute_coin_third.text = Html.fromHtml(Cxt.getStr(R.string.contribute_coin_mark, contributeNumber))
            if (list[2].userInfo.userLevel > 0) {
                level_container_third.visibility = View.VISIBLE
                level_image_third.setImageResource(getBackGroundLevelResource(list[2].userInfo.userLevel, LevelSize.Normal))
                if (list[2].userInfo.userLevel > 9) {
                    level_third.setPadding(0, 0, dip(4), 0)
                } else {
                    level_third.setPadding(0, 0, dip(9), 0)
                }
                level_third.text = list[2].userInfo.userLevel.toString()
            } else {
                level_container_third.visibility = View.GONE
            }
        }
        if (list.size == 0) {
            level_container_first.visibility = View.GONE
            level_container_second.visibility = View.GONE
            level_container_third.visibility = View.GONE
        }

    }

    private fun View.bindClick(uid: Int) {
        setOnClickListener {
            (activity as? VideoLiveRoomActivity)?.apply {
                showUserInfoDialog(roomInfo, uid) { type, info ->
                    if (type == 0) {
                        apiSpiceMgr.requestUserInfo(arrayOf(info.userId), true) { list ->
                            list.firstOrNull()?.let { info ->
                                showInputKey(info)
                            }
                        }
                    }
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    private fun defaultViewDisplay() {
        soft_second_view.visibility = View.VISIBLE
        soft_first_view.visibility = View.VISIBLE
        soft_third_view.visibility = View.VISIBLE
        contribute_name_first.setTextColor(resources.getColor(R.color.color_999999))
        contribute_name_second.setTextColor(resources.getColor(R.color.color_999999))
        contribute_name_third.setTextColor(resources.getColor(R.color.color_999999))
        contribute_name_first.paint.isFakeBoldText = false
        contribute_name_second.paint.isFakeBoldText = false
        contribute_name_third.paint.isFakeBoldText = false
        contribute_name_first.text = resources.getString(R.string.empty_contribute_txt)
        contribute_name_second.text = resources.getString(R.string.empty_contribute_txt)
        contribute_name_third.text = resources.getString(R.string.empty_contribute_txt)
        image_avatar_first.setOnClickListener { }
        image_avatar_second.setOnClickListener { }
        image_avatar_third.setOnClickListener { }
        //        image_avatar_first.viewColor = ContextCompat.getColor(image_avatar_first.context, R.color.color_f6f6f6)
//        image_avatar_second.viewColor = ContextCompat.getColor(image_avatar_second.context, R.color.color_f6f6f6)
//        image_avatar_third.viewColor = ContextCompat.getColor(image_avatar_third.context, R.color.color_f6f6f6)
        image_avatar_first.glideDisplay("", R.color.color_f6f6f6)
        image_avatar_second.glideDisplay("", R.color.color_f6f6f6)
        image_avatar_third.glideDisplay("", R.color.color_f6f6f6)
        level_second.text = null

        level_container_first.visibility = View.GONE
        level_container_second.visibility = View.GONE
        level_container_third.visibility = View.GONE
        contribute_coin_second.text = null
        contribute_coin_first.text = null
        contribute_coin_third.text = null
    }

    /**
     * 更新我的view
     */
    @Suppress("DEPRECATION")
    fun updateMeRankView(data: ContributionsInfo) {
        if (data.userRank?.rank ?: 0 in 1..100) {
            me_rank.textSize = 13.toFloat()
            me_rank.text = data.userRank.rank.toString()
        } else {
            me_rank.textSize = 10.toFloat()
            me_rank.text = resources.getString(R.string.not_at_rank)
        }
        me_image_avatar.glideDisplay(MeiUser.getSharedUser().info?.avatar.orEmpty(), MeiUser.getSharedUser().info?.gender.genderAvatar(MeiUser.getSharedUser().info?.isPublisher))
        me_contributes.text = Cxt.getStr(R.string.my_contribute, data.userRank.sendCount.toString())

        /**
         * 未上榜
         */
        val redColorText = "<font color = \"#FF4F00\">" + formatContribute(data.userRank.needSend.toLong(), "w") + "</font>"
        val layoutParams: RelativeLayout.LayoutParams = me_contributes.layoutParams as RelativeLayout.LayoutParams
        when (data.userRank.rank) {
            0 -> {//未上榜
                val needSendTxt = Cxt.getStr(R.string.distance_of_list, redColorText)
                last_rank_need_send.text = Html.fromHtml(needSendTxt)
                layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams.addRule(RelativeLayout.RIGHT_OF, me_image_avatar.id)
            }
            1 -> {//第一名
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)
                me_contributes.layoutParams = layoutParams
                last_rank_need_send.text = null
            }
            else -> {
                //已经上榜了
                val needSendTxt = Cxt.getStr(R.string.distance_of_last_person, redColorText)
                last_rank_need_send.text = Html.fromHtml(needSendTxt)
                layoutParams.removeRule(RelativeLayout.CENTER_VERTICAL)
                layoutParams.addRule(RelativeLayout.RIGHT_OF, me_image_avatar.id)
                me_contributes.layoutParams = layoutParams
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setSelected(type: Int) {
        data.clear()
        adapter.notifyDataSetChanged()
        currentType = type
        (activity as? MeiCustomBarActivity)?.apiSpiceMgr?.cancel(request)
        when (type) {
            REFRESH_TYPE_TODAY -> {
                val resDrawable = Cxt.getRes().getDrawable(R.drawable.shape_contribute_list_selected, null)
                resDrawable.setBounds(0, 0, resDrawable.minimumWidth, resDrawable.minimumHeight)
                today_contribute.setCompoundDrawables(null, null, null, resDrawable)
                total_contribute.setCompoundDrawables(null, null, null, null)

                today_contribute.setBackgroundResource(R.drawable.bg_contribute_title_left)
                total_contribute.setBackgroundResource(0)

                total_contribute_text.text = "今日互动"
                today_contribute.isSelected = true
                today_contribute.setTextColor(ContextCompat.getColor(today_contribute.context, R.color.color_333333))
                total_contribute.isSelected = false
                total_contribute.setTextColor(ContextCompat.getColor(today_contribute.context, R.color.color_999999))
                total_contribute.paint.isFakeBoldText = false
                today_contribute.paint.isFakeBoldText = true
            }
            else -> {
                val resDrawable = Cxt.getRes().getDrawable(R.drawable.shape_contribute_list_selected, null)
                resDrawable.setBounds(0, 0, resDrawable.minimumWidth, resDrawable.minimumHeight)
                total_contribute.setCompoundDrawables(null, null, null, resDrawable)
                today_contribute.setCompoundDrawables(null, null, null, null)

                total_contribute.setBackgroundResource(R.drawable.bg_contribute_title_right)
                today_contribute.setBackgroundResource(0)
                total_contribute_text.text = "累计互动"

                today_contribute.isSelected = false
                today_contribute.setTextColor(ContextCompat.getColor(today_contribute.context, R.color.color_999999))
                total_contribute.isSelected = true
                total_contribute.setTextColor(ContextCompat.getColor(today_contribute.context, R.color.color_333333))
                total_contribute.paint.isFakeBoldText = true
                today_contribute.paint.isFakeBoldText = false
            }

        }

    }


    fun refresh(isDaily: Boolean) {
        request.pageNo = 0
        defaultViewDisplay()
        adapter.clear()
        adapter.loadMoreModule.isEnableLoadMore = true
        val apiClient = (activity as? MeiCustomBarActivity)?.apiSpiceMgr
        (activity as? MeiCustomBarActivity)?.showLoading(true)
        request.isDaily = isDaily
        apiClient?.executeToastKt(request, success = {
            adapter.loadMoreModule.loadMoreComplete()
            if (it.isSuccess) {
                updateView(it.data, true, currentType)
            }
        }, failure = {
            adapter.loadMoreModule.loadMoreFail()
        }, finish = {
            (activity as? MeiCustomBarActivity)?.showLoading(false)
        })
    }

    private fun loadMore(pageNo: Int, type: Int) {
        request.pageNo = pageNo
        (activity as? SpiceHolder)?.apiSpiceMgr?.executeToastKt(request, success = {
            if (it.isSuccess) {
                if (type == currentType) {//滑动加载更多时切换tab导致数据请求重新开始
                    updateView(it.data, false, type)
                }
            }
        }, failure = {
            adapter.loadMoreModule.loadMoreFail()
        })
    }


}

