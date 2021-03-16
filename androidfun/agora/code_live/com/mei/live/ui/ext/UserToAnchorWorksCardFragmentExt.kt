package com.mei.live.ui.ext

import android.widget.RelativeLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import com.mei.GrowingUtil
import com.mei.live.ui.VideoLiveRoomActivity
import com.mei.live.ui.dialog.DATA_CARD_FROM_BILLBOARD
import com.mei.live.ui.dialog.adapter.TYPE_LOAD_MORE
import com.mei.live.ui.dialog.fragment.UserToAnchorWorksCardFragment
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.orc.ext.dip
import com.mei.orc.john.model.JohnUser
import com.mei.wood.extensions.executeKt
import com.mei.wood.util.AppManager
import com.net.network.chick.friends.WorkListRequest
import kotlinx.android.synthetic.main.fragment_works_use_see_pub_cards.*

/**
 *
 * @ProjectName:    dove
 * @Package:        com.mei.live.ui.ext
 * @ClassName:      UserToAnchorWorksCardFragmentExt
 * @Description:
 * @Author:         zxj
 * @CreateDate:     2020/6/28 18:03
 * @UpdateUser:
 * @UpdateDate:     2020/6/28 18:03
 * @UpdateRemark:
 * @Version:
 */
//恢复到最小的的值
fun UserToAnchorWorksCardFragment.restoreLayout() {
    card_head_frame.updateLayoutParams {
        height = dip(60)

    }
    card_head_frame.setPadding(dip(50), 0, 0, 0)
    //头像放大缩小
    fl_head_dialog_card.updateLayoutParams<RelativeLayout.LayoutParams> {
        height = dip(25)
        width = dip(40)
        tv_card_follow_anchor.isVisible = userInfo.isSelf == false
        more_btn.isVisible = false
        iv_roler_label.isVisible = true
        setMargins(dip(5), dip(25), 0, 0)
    }
    live_state.isVisible = false
}

//设置
fun UserToAnchorWorksCardFragment.setLayout(percent: Float, maxTitleHeight: Int, maxImage: Int, martTop: Int) {
    if (percent <= 0.5) {
        tv_card_follow_anchor.isVisible = false
        more_btn.isVisible = userInfo.isSelf == false && from != DATA_CARD_FROM_BILLBOARD
        iv_roler_label.isVisible = false
    }
    //标题栏变宽
    card_head_frame.apply {
        updateLayoutParams {
            height = (dip(50) + maxTitleHeight * percent).toInt()
        }
        setPadding(((dip(110) - (dip(40) * percent)).toInt()), 0, 0, 0)
    }
    live_state.isVisible = percent < 0.5f && userInfo.onlineStatus == 1 && from == DATA_CARD_FROM_BILLBOARD
    //头像放大缩小
    fl_head_dialog_card.updateLayoutParams<RelativeLayout.LayoutParams> {
        height = (dip(100) - (dip(55) * percent)).toInt()
        width = (dip(100) - (maxImage * percent)).toInt()
        setMargins(0, ((martTop * percent).toInt()), 0, 0)
    }
}

/**
 * 埋点
 */
fun UserToAnchorWorksCardFragment.growCard(clickType: String) {
    val viewType = if (workResult.list.size > 0 && userInfo.introduction.orEmpty().isNotEmpty()) {
        "有简介有作品"
    } else if (workResult.list.size > 0) {
        "有简介无作品"
    } else if (userInfo.introduction.orEmpty().isNotEmpty()) {
        "无简介有作品"
    } else {
        "无简介无作品"
    }
    val roomType = roomInfo.getRoomTypeForGrowingTrack()
    GrowingUtil.track("personal_datacard_click", "room_id", roomInfo.roomId, "user_id", roomInfo.createUser?.userId.toString(), "room_type", roomType, "click_type", clickType, "view_type", viewType)
}

/**
 * 判断当前用户是否在连麦中
 * @return 0 游客 1 主播 2 上麦用户
 */
fun getCurrLiveState(): Int {
    val liveVideoSplitFragment = (AppManager.getInstance().getTargetActivity(VideoLiveRoomActivity::class.java) as? VideoLiveRoomActivity)?.liveVideoSplitFragment
    return if (liveVideoSplitFragment?.upstreamUserIds.orEmpty().contains(JohnUser.getSharedUser().userID)) {
        if (liveVideoSplitFragment?.roomInfo?.isCreator == true) {
            1
        } else {
            2
        }
    } else {
        0
    }
}

/**
 * 获取更多作品
 */
fun UserToAnchorWorksCardFragment.requestLoadMoreWorks() {
    apiSpiceMgr.executeKt(WorkListRequest(userInfo.userId, workPage), success = { response ->
        response?.data?.list?.let { worksBean ->
            workPage = worksBean.nextPageNo
            hasMore = worksBean.hasMore
            worksBean.list.let {
                workResult.list.addAll(it)
                listItemInfo.addAll(it)
            }
            if (hasMore) {
                cardAdapter.loadMoreModule.loadMoreComplete()
            } else {
                if (workResult.list.size > 0) listItemInfo.add(TYPE_LOAD_MORE)
                cardAdapter.loadMoreModule.loadMoreEnd()
            }
        }
    })
}