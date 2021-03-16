package com.mei.orc.http.interceptor

import okhttp3.HttpUrl

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/2/20
 *
 *
 * 全局网络请求监听返回结果，如果处理登录态失效等等
 */
interface MeiInterceptor {

    /**
     * 全局网络请求
     *
     * @param url    网络请求结构
     * @param rtn    错误码
     * @param result 全部数据
     */
    fun handleResult(url: HttpUrl, rtn: Int, result: String)
}
