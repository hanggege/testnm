package com.mei.live.views

import com.mei.base.common.FOLLOW_USER_STATE
import com.mei.orc.event.postAction
import com.mei.orc.ui.toast.UIToast
import com.mei.wood.extensions.executeKt
import com.mei.wood.extensions.executeToastKt
import com.mei.wood.ui.MeiCustomActivity
import com.mei.wood.ui.MeiCustomBarActivity
import com.net.network.chick.friends.FollowDeleteRequest
import com.net.network.chick.friends.FollowExistRequest
import com.net.network.chick.friends.FollowFriendRequest

/**
 * Created by hang on 2020-02-20.
 */
/**
 * 判断是否是关注关系
 */
fun MeiCustomActivity.followExist(followId: Int, back: (Boolean) -> Unit) {
    apiSpiceMgr.executeKt(FollowExistRequest(followId), success = {
        if (it.data?.isFollow == true) {
            back(true)
        } else {
            back(false)
        }
    }, failure = {
        back(false)
    })
}

/**
 * 关注好友
 */
fun MeiCustomBarActivity.followFriend(followId: Int, source: Int, fromId: String, back: (Boolean) -> Unit = {}) {
    apiSpiceMgr.executeToastKt(FollowFriendRequest(followId, source, fromId), success = {
        if (it.isSuccess) {
            postAction(FOLLOW_USER_STATE, Pair(followId, true))
            UIToast.toast("关注成功")
            back(true)
        } else {
            back(false)
        }
    }, failure = {
        back(false)
    })
}

/**
 * 取消关注
 */
fun MeiCustomBarActivity.followDelete(followId: Int, back: (Boolean) -> Unit = {}) {
    apiSpiceMgr.executeToastKt(FollowDeleteRequest(followId), success = {
        if (it.isSuccess) {
            postAction(FOLLOW_USER_STATE, Pair(followId, false))
            UIToast.toast("取消关注成功")
            back(true)
        } else {
            back(false)
        }
    }, failure = {
        back(false)
    })
}
