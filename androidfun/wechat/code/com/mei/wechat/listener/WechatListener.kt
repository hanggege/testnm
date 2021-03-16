package com.mei.wechat.listener

/**
 * Created by Ling on 2018/6/28.
 * @描述：当关注微信时，需要在点击时回调，回调数据包括微信的数据和是否是关注
 */
interface WechatListener<T> {
    //微信获取的数据，一般在点击按钮时回调
    fun onBackWechatJasn(t: T?)

    //是否关注成功
    fun onFollow(b: Boolean)

}