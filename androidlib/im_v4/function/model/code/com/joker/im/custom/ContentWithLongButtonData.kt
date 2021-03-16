package com.joker.im.custom

/**
 * Created by hang on 2019-09-25.
 */
class ContentWithLongButtonData : CustomBaseData() {


    class Result(data: ContentWithLongButtonData) : CustomInfo<ContentWithLongButtonData>(CustomType.content_with_long_button.name, data)

    override val summary: String
        get() = title ?: ""

    override val copySummary: String
        get() = ""


    /**
     * 标题
     */
    var title: String? = ""

    /**
     * 文字
     */
    var content: String? = ""

    var long_button: IMButton? = null

}