package com.joker.im.custom

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/18.
 */

abstract class CustomBaseData {

    var type: CustomType = CustomType.invalid

    /**
     * 获取消息摘要
     */
    abstract val summary: String

    /**
     * 用来复制的消息
     */
    abstract val copySummary: String

}
