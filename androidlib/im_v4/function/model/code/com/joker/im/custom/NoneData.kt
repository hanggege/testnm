package com.joker.im.custom

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/18.
 */

class NoneData : CustomBaseData() {

    class Result : CustomInfo<NoneData>()

    override val summary: String
        get() = "消息类型暂未支持"

    override val copySummary: String
        get() = ""
}
