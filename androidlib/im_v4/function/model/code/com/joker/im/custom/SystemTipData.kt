package com.joker.im.custom


/**
 * Created by 杨强彪 on 2017/4/20.
 * @描述：系统通知
 */
class SystemTipData(var text: String = "") : CustomBaseData() {
    class Result(type: CustomType, data: SystemTipData) : CustomInfo<SystemTipData>(type.name, data)


    override val summary: String
        get() = text //To change initializer of created properties use File | Settings | File Templates.
    override val copySummary: String
        get() = text //To change initializer of created properties use File | Settings | File Templates.
}