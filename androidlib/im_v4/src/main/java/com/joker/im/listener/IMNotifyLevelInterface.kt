package com.joker.im.listener

/**
 * 佛祖保佑         永无BUG
 *
 * Created by joker on 2017/2/19.
 */

interface IMNotifyLevelInterface {

    /**
     * 全都不通知
     */
    fun isAllIgnore(): Boolean = false

    /**
     * 不通知指定ID
     */
    fun IgnoreByID(): String = ""
}
