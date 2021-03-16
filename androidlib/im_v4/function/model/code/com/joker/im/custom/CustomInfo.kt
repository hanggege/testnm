package com.joker.im.custom

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/17.
 */

open class CustomInfo<T : CustomBaseData> {

    private var type: String = ""
    var data: T? = null

    constructor() {}

    constructor(type: String, data: T) {
        this.type = type
        this.data = data
    }

    fun getType(): CustomType {
        return CustomType.parseValue(type)
    }

}
