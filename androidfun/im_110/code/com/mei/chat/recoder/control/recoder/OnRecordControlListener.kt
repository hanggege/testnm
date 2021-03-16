package com.mei.chat.recoder.control.recoder

import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019/3/12
 */
interface OnRecordControlListener {

    fun onStart(file: File?)

    /**
     * @param type 0表示松手，1表示超过最大时长
     * @param file
     */
    fun onFinish(type: Int, file: File?, time: Int)

    fun onFail(code: Int, errorMsg: String)

    fun onAudioVibration(vibration: Int)

}
