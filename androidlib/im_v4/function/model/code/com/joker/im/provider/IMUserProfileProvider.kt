package com.joker.im.provider

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/28
 *
 * Tencent的用户信息有很大的问题，ios能获取到用户信息，Android获取不全，昵称都没有
 */

var imUserProfile: IMUserProfileProvider? = null

fun registerIMUserProfile(provider: IMUserProfileProvider) {
    imUserProfile = provider
}

interface IMUserProfileProvider {

    fun getNickName(id: String): String
    fun getFaceUrl(id: String): String

}