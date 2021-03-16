package com.mei.wood.cache

import com.joker.im.provider.IMUserProfileProvider
import com.mei.orc.util.string.getInt

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/28
 */
class IMUserProfileImpl : IMUserProfileProvider {

    override fun getNickName(id: String): String {
        return getCacheUserInfo(id.getInt())?.nickname.orEmpty()
    }

    override fun getFaceUrl(id: String): String {
        return getCacheUserInfo(id.getInt())?.avatar.orEmpty()
    }


}