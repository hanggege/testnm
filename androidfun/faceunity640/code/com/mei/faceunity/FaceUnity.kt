package com.mei.faceunity

import com.mei.init.spiceHolder
import com.mei.orc.Cxt
import com.mei.orc.ui.toast.UIToast
import com.mei.orc.util.json.json2Obj
import com.mei.orc.util.json.toJson
import com.mei.orc.util.string.stringFromRawResourceId
import com.mei.wood.R
import com.mei.wood.extensions.executeKt
import com.net.model.chick.tab.tabbarConfig
import com.net.model.config.appConfigMap
import com.net.model.config.requestConfigLoad
import com.net.network.config.AppConfigUpdateRequest

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */

private const val SAVE_SERVICE_KEY = "user_face_skin"

fun getServiceKey(): String {
    var key = tabbarConfig.customBeautyConfigKey
    if (key.isEmpty()) key = SAVE_SERVICE_KEY
    return key
}

/**
 * 获取美颜保存的数据
 * 保证本地数据最新
 * 如果本地没有数据从网络拉取
 * 网络拉取到保存本地
 */
@Suppress("UNCHECKED_CAST")
fun loadFaceUnityInfo(): Map<String, Any> {
    /** 优先从服务器获取美颜效果，因为有可能最新的信息未保存成功 **/
    var jsonData = appConfigMap[getServiceKey()].orEmpty()

    if (jsonData.isEmpty()) {
        /** 如果服务器没有或者未拉取到的话，用App打包的默认，且重新拉一下服务器配置保存本地，方便下次使用 **/
        jsonData = stringFromRawResourceId(R.raw.face_default).orEmpty()
        requestConfigLoad(getServiceKey())
    }
    return (jsonData.json2Obj(Map::class.java) as? Map<String, Any>) ?: mapOf()
}

/**
 * 获取最新的美颜配置
 */
@Suppress("UNCHECKED_CAST")
fun loadNewFaceUnityInfo(serviceKey: String = getServiceKey(), back: (Map<String, Any>) -> Unit) {
    requestConfigLoad(serviceKey) {
        back((it.json2Obj(Map::class.java) as? Map<String, Any>) ?: mapOf())
    }
}

fun saveFaceUnityInfo() {
    val reportMap = hashMapOf<String, Any>()
    reportMap.putAll(cacheFaceSkinParams)
    reportMap.putAll(cacheFaceShapeParams)
    reportMap.putAll(cacheFaceFilterParams)
    val json = reportMap.toJson().orEmpty()
    if (reportMap.isNotEmpty()) {
        spiceHolder().apiSpiceMgr.executeKt(AppConfigUpdateRequest(getServiceKey(), json), success = {
            if (it.isSuccess) {
                appConfigMap[getServiceKey()] = json
                UIToast.toast(Cxt.get(), "保存成功")
            }
        })
    }
}


/**
 * 用户进行的美肤编辑操作
 */
val cacheFaceSkinParams = mutableMapOf<String, Float>()

/**
 * 用户进行的美型编辑操作
 */
val cacheFaceShapeParams = mutableMapOf<String, Float>()

/**
 * 用户进行的滤镜编辑操作
 */
val cacheFaceFilterParams = mutableMapOf<String, Any>()
