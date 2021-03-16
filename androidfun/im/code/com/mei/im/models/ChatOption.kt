package com.mei.im.models

/**
 * 佛祖保佑         永无BUG

 * Created by joker on 2016/12/31.
 */

enum class ChatOption constructor(var desc: String) {
    copy("复制"),
    delete("删除"),
    recall("撤回"),
    dropTop("置顶"),
    cancelTop("取消置顶"),
    readAll("标为已读")
}
