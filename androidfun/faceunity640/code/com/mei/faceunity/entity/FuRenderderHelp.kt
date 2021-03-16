package com.mei.faceunity.entity

import com.faceunity.FURenderer
import com.faceunity.entity.Filter
import com.faceunity.param.BeautificationParam
import com.mei.faceunity.loadFaceUnityInfo

/**
 * 初始化initFuRenderer
 */
fun FURenderer.initFuRenderer(): FURenderer {
    val loadSkinMap: Map<String, Any> = loadFaceUnityInfo()
    if (loadSkinMap.isEmpty()) {
        return this
    }
    loadSkinMap.forEach {
        if (it.key == BeautificationParam.FILTER_NAME) {
            this.onFilterNameSelected((it.value as? String) ?: Filter.Key.ORIGIN)
        }
        (it.value as? Double)?.toFloat()?.let { value ->
            when (it.key) {
                // 滤镜
                BeautificationParam.FILTER_LEVEL -> this.onFilterLevelSelected(value)
                //美肤
                BeautificationParam.SKIN_DETECT -> this.onSkinDetectSelected(value)
                BeautificationParam.BLUR_TYPE -> this.onBlurTypeSelected(value)
                BeautificationParam.BLUR_LEVEL -> this.onBlurLevelSelected(value)
                BeautificationParam.COLOR_LEVEL -> this.onColorLevelSelected(value)
                BeautificationParam.RED_LEVEL -> this.onRedLevelSelected(value)
                BeautificationParam.EYE_BRIGHT -> this.onEyeBrightSelected(value)
                BeautificationParam.TOOTH_WHITEN -> this.onToothWhitenSelected(value)
                //美型
                BeautificationParam.EYE_ENLARGING -> this.onEyeEnlargeSelected(value)
                BeautificationParam.CHEEK_THINNING -> this.onCheekThinningSelected(value)
                BeautificationParam.CHEEK_NARROW -> this.onCheekNarrowSelected(value)
                BeautificationParam.CHEEK_SMALL -> this.onCheekSmallSelected(value)
                BeautificationParam.CHEEK_V -> this.onCheekVSelected(value)
                BeautificationParam.INTENSITY_NOSE -> this.onIntensityNoseSelected(value)
                BeautificationParam.INTENSITY_CHIN -> this.onIntensityChinSelected(value)
                BeautificationParam.INTENSITY_FOREHEAD -> this.onIntensityForeheadSelected(value)
                BeautificationParam.INTENSITY_MOUTH -> this.onIntensityMouthSelected(value)
                else -> {
                }
            }
        }
    }
    return this
}