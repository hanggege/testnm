package com.joker.im.custom

/**
 * Created by hang on 2019-09-25.
 */
class ContentWithTwoButtonData : CustomBaseData() {


    class Result(data: ContentWithTwoButtonData) : CustomInfo<ContentWithTwoButtonData>(CustomType.content_with_two_button.name, data)

    override val summary: String
        get() = title

    override val copySummary: String
        get() = ""


    /**
     * 标题
     */
    var title: String = ""

    /**
     * 文字
     */
    var content: String = ""

    /**
     * 左按钮
     */
    var left_button: IMButton? = null

    /**
     * 右按钮
     */
    var right_button: IMButton? = null

    var msg_id = ""
}

class IMButton {

    var text: String = ""

    /**
     * 0 支持当前业务需求
     * 1 支持以后拓展需求
     */
    var link_type: Int = 0

    var url_link: String = ""

}