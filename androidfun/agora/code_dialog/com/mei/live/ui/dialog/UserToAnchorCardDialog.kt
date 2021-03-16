package com.mei.live.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import com.mei.GrowingUtil
import com.mei.live.ui.dialog.fragment.UserToAnchorCardFragment
import com.mei.live.ui.dialog.fragment.UserToAnchorWorksCardFragment
import com.mei.live.ui.dialog.fragment.UserToPresenterCardFragment
import com.mei.live.ui.getRoomTypeForGrowingTrack
import com.mei.orc.dialog.BottomDialogFragment
import com.mei.wood.R
import com.net.model.room.RoomInfo
import com.net.model.user.DataCard


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-06
 * 知心达人用户资料卡
 * @param back type   0:at   1:送礼   2:送礼物加好友
 */
fun FragmentActivity.showUserToAnchorCardDialog(roomInfo: RoomInfo, dataCard: DataCard, from: String = "", back: (type: Int, info: DataCard) -> Unit = { _, _ -> }) {
    if (supportFragmentManager.findFragmentByTag("UserToAnchorCardDialog") == null) {
        UserToAnchorCardDialog().apply {
            this.backs = {
                back(it, dataCard)
            }
            this.from = from
            this.dataCard = dataCard
            this.roomInfo = roomInfo
        }.show(supportFragmentManager, "UserToAnchorCardDialog")
    }
}

/**
 * 用户查看主播或者主播
 */
class UserToAnchorCardDialog : BottomDialogFragment() {
    var dataCard: DataCard = DataCard()
    var roomInfo: RoomInfo = RoomInfo()
    var from = ""
    var backs: (type: Int) -> Unit = {}

    //没有作品，没有用户间接标签
    private val userToAnchorCardFragment by lazy {
        UserToAnchorCardFragment().apply {
            this.userInfo = dataCard
            this.roomInfo = this@UserToAnchorCardDialog.roomInfo
            this.back = backs
            this.dismissBack = {
                dismissAllowingStateLoss()
            }
        }
    }

    //有作品，有用户间接标签
    private val userToAnchorWorksCardFragment by lazy {
        UserToAnchorWorksCardFragment().apply {
            this.userInfo = dataCard
            this.roomInfo = this@UserToAnchorCardDialog.roomInfo
            this.from = this@UserToAnchorCardDialog.from
            this.back = backs
            this.dismissBack = {
                dismissAllowingStateLoss()
            }
        }
    }

    private val userToPresenterCardFragment by lazy {
        UserToPresenterCardFragment().apply {
            this.dataCard = this@UserToAnchorCardDialog.dataCard
            this.roomInfo = this@UserToAnchorCardDialog.roomInfo
            this.back = backs
            this.dismissBack = {
                dismissAllowingStateLoss()
            }
        }
    }

    private val billboardToPresentCardFragment by lazy {
        BillboardToPresentCardFragment().apply {
            this.dataCard = this@UserToAnchorCardDialog.dataCard
            this.roomInfo = this@UserToAnchorCardDialog.roomInfo
            this.back = backs
            this.dismissBack = {
                dismissAllowingStateLoss()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.view_user_to_anchor_info_card, container, false)
        dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (dataCard.userId == 0) {
            dismissAllowingStateLoss()
            return
        }
        //作品为空，简介为空，删除领域为空，
        val fragment: Fragment = if (dataCard.groupId > 0) {
            if (from == DATA_CARD_FROM_BILLBOARD) {
                billboardToPresentCardFragment
            } else {
                userToPresenterCardFragment
            }
        } else if (dataCard.introduction.orEmpty().isNotEmpty() || dataCard.worksCount > 0) {
            userToAnchorWorksCardFragment
        } else {
            userToAnchorCardFragment
        }
        val viewType = if (dataCard.introduction.orEmpty().isNotEmpty() && dataCard.worksCount > 0) {
            "有简介有作品"
        } else if (dataCard.introduction.orEmpty().isNotEmpty()) {
            "有简介无作品"
        } else if (dataCard.worksCount > 0) {
            "无简介有作品"
        } else {
            "无简介无作品"
        }
        GrowingUtil.track("personal_datacard_view", "room_id", roomInfo.roomId,
                "user_id", roomInfo.createUser?.userId.toString(), "room_type", roomInfo.getRoomTypeForGrowingTrack(), "view_type", viewType)
        childFragmentManager.commitNow(allowStateLoss = true) {
            replace(R.id.frame_user_to_anchor_info_card, fragment)
        }
    }
}