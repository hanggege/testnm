package com.mei.wood.dialog

import android.graphics.Color
import android.view.View

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/8
 */
@Suppress("EqualsOrHashCode")
data class DialogData @JvmOverloads constructor(
        val message: String? = "",
        val cancelStr: String? = "取消",//传空则不显示
        val okStr: String? = "确定",//传空则不显示
        val canceledOnTouchOutside: Boolean = true,//点击其它地方是否关闭
        val title: String? = "",//传空则不显示
        val customView: View? = null,//自定义View,设置这个了，上面其实参数都失效
        val key: String = "",//弹框是的key，是否
        val timeOut: Int = 0, //单位秒 做大显示时间
        val okTimeOut: Int = 0, //单位秒 显示在cancelStr后面的倒计时，如 取消(20s)
        val messageHint: String? = "", //内容提示
        val backCanceled: Boolean = true,//是否支持返回键退出
        val cancelColor: Int = Color.parseColor("#989898"),//返回按键颜色
        val okColor: Int = Color.parseColor("#333333"),//确认按键是否关闭
        val isCustomViewGravityTop: Boolean = false,
        val ignoreBackground: Boolean = false,//是否后台忽略
) {
    /**
     * 判断数据是否一致，看是否是出现了多次重复弹出
     */
    override fun equals(other: Any?): Boolean {
        return if (other is DialogData) {
            if (key.isNotEmpty()) {
                return key == other.key
            } else {
                message == other.message
                        && cancelStr == other.cancelStr
                        && okStr == other.okStr
                        && canceledOnTouchOutside == other.canceledOnTouchOutside
                        && title == other.title
            }
        } else {
            false
        }
    }
}