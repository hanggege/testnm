package com.mei.wood.dialog

import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.save.getLongMMKV
import com.mei.orc.util.save.getStringMMKV
import com.mei.orc.util.save.putMMKV

/**
 * @author caowei
 * @blog https://646030315.github.io
 * @email 646030315@qq.com
 * Created on 2017/12/20.
 */


/**
 * 获取当前频道或者房间保存的验证通过的code，获取到了就说明之前验证成功了，就不需要再验证了
 */
fun getValidateCode(roomId: String) =
        if (checkCodeIsEffective(roomId)) "effective_code_${roomId}_${JohnUser.getSharedUser().userID}".getStringMMKV()
        else ""

/**
 * 保存输入的口令时间，四小时不再输入（时间后台配置 单位分钟）
 */
fun saveEffectiveCodeAndTime(roomId: String, code: String) {
    "effective_time_${roomId}_${JohnUser.getSharedUser().userID}".putMMKV(System.currentTimeMillis())
    "effective_code_${roomId}_${JohnUser.getSharedUser().userID}".putMMKV(code)
}

/**
 * 检查上次输入的口令是否有效
 */
fun checkCodeIsEffective(roomId: String): Boolean {
    val lastTime = "effective_time_${roomId}_${JohnUser.getSharedUser().userID}".getLongMMKV()
    val between = (System.currentTimeMillis() - lastTime) / 1000 / 60
    return lastTime != 0L && between <= 240

}