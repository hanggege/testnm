package com.mei.chat.recoder.control.uistate

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/13
 */
interface IUIStateControl {

    enum class STATE {
        DEF, //默认
        DOWN, //按下说话
        DOWN_CANCEL, //滑动取消状态
        DOWN_TIME_OUT, //按住超时
        DOWN_TIME_SHORT, //按住说话过短
        UP_CANCEL, //抬起取消
        UP//正常抬起
    }

    fun stateChange(oldState: STATE, nowState: STATE)

    fun onAudioVibration(vibration: Int)

}
