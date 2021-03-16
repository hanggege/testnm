package com.joker.im.message

import com.google.gson.Gson
import com.joker.im.Message
import com.joker.im.custom.CustomInfo
import com.joker.im.custom.CustomType
import com.joker.im.custom.chick.ChickCustomData
import com.tencent.imsdk.TIMCustomElem
import com.tencent.imsdk.TIMMessage
import org.json.JSONObject

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-06-18
 */
class CustomMessage : Message {

    private var cacheCustomInfo: CustomInfo<*>? = null
    var customInfo: CustomInfo<*>?
        set(_) {}
        get() {
            if (cacheCustomInfo == null && timMessage.elementCount > 0) {
                try {
                    val json = (timMessage.getElement(0) as? TIMCustomElem)?.data?.toString(Charsets.UTF_8).orEmpty()
                    Gson().fromJson<CustomInfo<*>>(json, customMsgType.cls)?.let {
                        cacheCustomInfo = it
                        cacheCustomInfo?.data?.type = it.getType()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return cacheCustomInfo
        }


    private var cacheCustomType: CustomType? = null
    var customMsgType: CustomType
        set(_) {}
        get() {
            if (cacheCustomType == null) {
                try {
                    val json = (timMessage.getElement(0) as? TIMCustomElem)?.data?.toString(Charsets.UTF_8).orEmpty()
                    if (json.isNotEmpty()) cacheCustomType = CustomType.parseValue(JSONObject(json).getString("type"))
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            return cacheCustomType ?: CustomType.invalid
        }


    /** 是否是已支持的消息类型 **/
    fun isSupportMsg(): Boolean = customMsgType != CustomType.invalid

    constructor(msg: TIMMessage) : super(msg)

    constructor(info: CustomInfo<*>) : super(TIMMessage().apply {
        addElement(TIMCustomElem().apply {
            data = Gson().toJson(info)?.toByteArray() ?: ByteArray(0)
        })
    }) {
        cacheCustomInfo = info
    }

    override fun getSummary(): String =
            if (customMsgType == CustomType.call_status_changed) {
                val data = customInfo?.data as? ChickCustomData
                if (data != null) {
                    "${if (data.exclusiveResult?.special == true) "[专属服务]" else "[私密连线]"}${if (isSelf) data.exclusiveResult?.forSender.orEmpty() else data.exclusiveResult?.forReceiver.orEmpty()}"
                } else ""
            } else if (customMsgType == CustomType.invite_up || customMsgType == CustomType.apply_exclusive) {
                (customInfo?.data as? ChickCustomData)?.pushContent.orEmpty()
            } else if (customMsgType == CustomType.general_card && customInfo?.data?.summary.isNullOrEmpty()) {
                (customInfo?.data as? ChickCustomData)?.card?.title.orEmpty().joinToString(separator = "") { it.text }
            } else customInfo?.data?.summary ?: ""


    override fun getCopySummary(): String = customInfo?.data?.copySummary ?: ""


}