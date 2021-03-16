package com.mei.im.push

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.tencent.imsdk.utils.IMFunc
import com.xiaomi.mipush.sdk.MiPushMessage
import com.xiaomi.mipush.sdk.PushMessageHelper


/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/5/7
 */
class PushBridgeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            val payload = when {
                IMFunc.isBrandHuawei() -> {
                    // TODO: joker 2020/5/9 https://github.com/tencentyun/TIMSDK/issues/354  这里目前收不到消息，需要等IM支持，已提工单
                    val ext = (intent.extras?.get("ext") as? String).orEmpty()
                    intent.extras?.keySet().orEmpty().forEach {
                        Log.e("infoExt", "extKey: $it  value: ${intent.getStringExtra(it)}")
                    }
                    ext
                }
                IMFunc.isBrandXiaoMi() -> {
                    val bundle = intent.extras
                    val miPushMessage = bundle?.getSerializable(PushMessageHelper.KEY_MESSAGE) as? MiPushMessage
                    miPushMessage?.extra?.get("ext").orEmpty()
                }
                IMFunc.isBrandOppo() -> {
                    /** oppo取数据有点特别，其它都是在ext中取，而oppo是按照key-valu存储，需要先map包起来 **/
                    val keys = intent.extras?.keySet().orEmpty()
                    var result = "{"
                    keys.forEachIndexed { index, it ->
                        val value = intent.getStringExtra(it).orEmpty().trim()
                        val end = if (index < keys.size - 1) "," else ""
                        /** 这里存在value是一个json对象传过来的，所以自己拼接json **/
                        result += if (value.startsWith("{") || value.startsWith("[")) {
                            "\"$it\":${intent.getStringExtra(it).orEmpty()} $end"
                        } else {
                            "\"$it\":\"${intent.getStringExtra(it).orEmpty()}\" $end"
                        }

                    }
                    result += "}"
                    result
                }
                else -> {
                    intent.getStringExtra("ext").orEmpty()
                }
            }

            Log.e("infoExt", ": $payload")
            startActivity(Intent(this, Class.forName("com.mei.splash.ui.SplashActivity")).apply {
                putExtra("getuipayload", payload)
                putExtra("from", "im_push")
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        finish()
    }
}