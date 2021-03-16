package com.mei.chat.recoder.callback

import java.io.File

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by zzw on 2019/3/12
 */
interface OnRecordListener {
    fun onFinish(file: File?, time: Int)

    fun onStart(file: File?)
}
