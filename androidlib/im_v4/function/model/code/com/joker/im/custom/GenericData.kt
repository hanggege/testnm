package com.joker.im.custom

import android.text.TextUtils

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2017/2/21.
 */

class GenericData : CustomBaseData {

    class Result(type: CustomType, data: GenericData) : CustomInfo<GenericData>(type.name, data)

    var content: String = ""
    var title: String = ""
    var image: String = ""
    var link_url: String = ""
    var extra: String? = ""
    var special_text: String = ""

    constructor() {}


    constructor(title: String, content: String, link_url: String) {
        this.content = content
        this.title = title
        this.link_url = link_url
    }

    constructor(title: String?, content: String?, image: String?, link_url: String?) {
        this.content = content ?: ""
        this.title = title ?: ""
        this.image = image ?: ""
        this.link_url = link_url ?: ""
    }

    constructor(title: String, content: String, image: String, link_url: String, extra: String) {
        this.content = content
        this.title = title
        this.image = image
        this.link_url = link_url
        this.extra = extra
    }

    constructor(title: String, content: String, image: String, link_url: String, extra: String, special_text: String) {
        this.content = content
        this.title = title
        this.image = image
        this.link_url = link_url
        this.extra = extra
        this.special_text = special_text
    }


    override val summary: String
        get() = if (TextUtils.isEmpty(title)) content else title + "\n" + content

    override val copySummary: String
        get() = if (TextUtils.isEmpty(title)) content else title + "\n" + content
}
