package com.mei.wood.util

import android.content.Context
import android.net.wifi.WifiManager
import android.text.TextUtils
import com.mei.init.meiApplication
import com.mei.orc.channel.channelId
import com.mei.orc.http.retrofit.RetrofitClient
import com.mei.orc.john.model.CookieModel
import com.mei.orc.john.model.JohnUser
import com.mei.orc.util.http.urlEncode
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.mei.orc.util.string.decodeUrl
import com.mei.orc.util.string.stringConcate
import com.mei.provider.ProjectExt
import com.mei.wood.BuildConfig
import com.mei.wood.ext.Constants
import java.util.*

/**
 *
 * @author Created by Ling on 2019-08-15
 */

object MeiUtil {

    /**-----------------------参数⬇----------------------------**/

    /**
     * 获取内链后面参数（一个），多会是json，所以转一下
     */
    @JvmOverloads
    fun getOneID(url: String, patternStr: String, needDecode: Boolean = true): String {
        val map = Regex(patternStr).findAll(url).toList().flatMap(MatchResult::groupValues).filterIndexed { index, _ -> index != 0 }
        return if (map.size == 1) {
            if (needDecode) map[0].decodeUrl(true)
            else map[0]
        } else {
            ""
        }
    }

    fun <T> getOneID(url: String, patternStr: String, cls: Class<T>, defaultValue: T): T {
        val id = getOneID(url, patternStr)
        return if (id.isNotEmpty()) {
            parseIntentExtraType(id, cls, defaultValue)
        } else defaultValue
    }

    /**
     * 获取内链后面json
     * */
    fun <T> getJsonObject(url: String, patternStr: String, cls: Class<T>): T? {
        return getOneID(url, patternStr).json2Obj(cls)
    }

    /**
     * 获取内链后面json
     * */
    inline fun <reified T : Any> getJsonObject(url: String, patternStr: String): T? {
        return getOneID(url, patternStr).json2Obj()
    }

    /**
     * 支持两条正则的一个参数
     */
    fun getOneID(url: String, patternStr1: String, patternStr2: String): String {
        return if (url.matches(patternStr2.toRegex())) {
            getOneID(url, patternStr2)
        } else {
            getOneID(url, patternStr1)
        }
    }

    fun <T> getOneID(url: String, patternStr1: String, patternStr2: String, cls: Class<T>, defaultValue: T): T {
        return if (url.matches(patternStr2.toRegex())) {
            getOneID(url, patternStr2, cls, defaultValue)
        } else {
            getOneID(url, patternStr1, cls, defaultValue)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> parseIntentExtraType(obj: Any?, cls: Class<T>, defaultValue: T): T {
        if (obj == null) return defaultValue
        return try {
            when (cls) {
                String::class.java -> obj.toString() as T

                Int::class.java -> obj.toString().toInt() as T

                Long::class.java -> obj.toString().toLong() as T

                Short::class.java -> obj.toString().toShort() as T

                Byte::class.java -> obj.toString().toByte() as T

                Double::class.java -> obj.toString().toDouble() as T

                Float::class.java -> obj.toString().toFloat() as T

                else -> obj.toString().json2Obj(cls) ?: defaultValue
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
    }


    /**
     * 正则获取多个参数的方法，一个参数的用上边那个[getOneID]
     * @param url 待匹配的值
     * @param patternStr 正则表达式
     * @param num 想要得到几个参数，传0及以下会修正为1
     */
    fun getRegexId(url: String, patternStr: String, num: Int): Array<String> {
        val size = num.coerceAtLeast(1)
        val map = Regex(patternStr).findAll(url).toList().flatMap(MatchResult::groupValues).filterIndexed { index, _ -> index != 0 }
        return if (size == map.size) {
            map.toTypedArray()
        } else {
            arrayOfNulls<String>(size).map { it.orEmpty() }.toTypedArray()
        }
    }

    /**
     * 获取正则后的参数，然后通过"/"切割成一个数组
     */
    fun getRegexId2(url: String, patternStr: String): Array<String> {
        val str = getOneID(url, patternStr)
        val split = str.split("/")
        return split.toTypedArray()
    }
    /**-----------------------参数⬆----------------------------**/


    /**
     * 判断是否是官方账号
     *
     * @param userId 导师账号
     * @return 官方账号为：81、99、800、95
     */
    fun isOfficialID(userId: Int): Boolean {
        return userId < 1000
    }

    fun isB2C(type: String): Boolean {
        return "b2c".equals(type, ignoreCase = true)
    }

    /**
     * 判断是否是大陆用户
     */
    fun isMainLand(): Boolean {
        val country = Locale.getDefault().country.toLowerCase()
        return "cn".equals(country, ignoreCase = true)
    }

    /**
     * url拼接基础参数
     */
    fun appendGeneraUrl(url: String?): String {
        if (url.isNullOrEmpty()) {
            return ""
        }
        return appendParamsUrl(
                url,
                "version" to BuildConfig.VERSION_NAME,
                "login_user_id" to JohnUser.getSharedUser().getUserID().toString(),
                "session_id" to JohnUser.getSharedUser().sessionID,
                "phone_sn" to JohnUser.getSharedUser().getPhoneSN(),
                "ssid" to getSSID(),
                "app_channel" to channelId,
                "push_client_id" to JohnUser.getSharedUser().getuiPushClientId,
                //  增加拉取web页面时user_agent字段
                "user_agent" to ProjectExt.WEBVIEWUserAgent,
                "cookie" to CookieModel.parseCookie().toJson().orEmpty().urlEncode(),
                "client_type" to Constants.CLIENT_TYPE,
                "is_test" to RetrofitClient.isTestClient().toString()
        )
    }

    private var ssid = ""

    fun getSSID(): String {
        if (ssid.isEmpty()) {
            ssid = getWifiSSID()
        }
        return ssid
    }

    private fun getWifiSSID(): String {
        val wm = meiApplication().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager?
        if (wm != null) {
            val wifiInfo = wm.connectionInfo
            if (wifiInfo != null) {
                val s = wifiInfo.ssid
                if (s.length > 2 && s[0] == '"' && s[s.length - 1] == '"') {
                    return s.substring(1, s.length - 1)
                }
            }
        }
        return ""
    }


    /**
     * url拼接基础参数  分享专用 去除sessionid和cookie
     */
    fun appendShareUrl(url: String?): String {
        if (url.isNullOrEmpty()) {
            return ""
        }
        return appendParamsUrl(
                url,
                "version" to BuildConfig.VERSION_NAME,
                "login_user_id" to JohnUser.getSharedUser().getUserID().toString(),
                "phone_sn" to JohnUser.getSharedUser().getPhoneSN(),
                "app_channel" to channelId,
                "push_client_id" to JohnUser.getSharedUser().getuiPushClientId,
                //  增加拉取web页面时user_agent字段
                "user_agent" to ProjectExt.WEBVIEWUserAgent,
                "client_type" to Constants.CLIENT_TYPE,
                "is_test" to RetrofitClient.isTestClient().toString()
        )
    }


    /**
     * url添加参数
     *
     * @param url
     * @param key_value 一对一添加，但有单个没有对应的value时，不做处理
     * @return
     */
    fun appendParamsUrl(url: String?, vararg key_value: Pair<String, String>): String {
        if (url.isNullOrEmpty()) {
            return ""
        }
        var resultUrl = url
        val tag = if (resultUrl.contains("?")) "&" else "?"
        var i = 0
        while (i < key_value.size) {
            resultUrl = if (i == 0) {
                stringConcate(resultUrl, tag, key_value[i].first, "=", key_value[i].second)
            } else {
                stringConcate(resultUrl, "&", key_value[i].first, "=", key_value[i].second)
            }
            i++
        }
        return resultUrl.orEmpty()
    }

    /**
     * 订单生成   加上前面10个界面名字
     */
    fun appendOrderRefer(refer: String): String {
        val list = AppManager.getInstance().activityNameList
        return if (refer.isNotEmpty()) {
            stringConcate(refer, ",", TextUtils.join("|", list))
        } else {
            TextUtils.join("|", list)
        }
    }
}