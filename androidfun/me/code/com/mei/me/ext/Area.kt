package com.mei.me.ext

import android.content.Context
import com.mei.orc.util.json.jsonToList
import okio.buffer
import okio.source


/**
 * Created by hang on 2019-12-12.
 */
data class Area(val province: String, val cities: List<String>)

fun Context.getAreaList() = assets.open("area.json").source().buffer().readUtf8().jsonToList(Area::class.java)

val professionList = arrayListOf(
        "在校学生",
        "计算机/IT",
        "电子/仪器",
        "通信技术",
        "销售",
        "市场拓展",
        "公关/商务",
        "采购/贸易",
        "客户服务",
        "人力/行政",
        "高级管理",
        "制造/加工",
        "质控/安检",
        "工程机械",
        "技工",
        "财会/审计",
        "金融",
        "装修/物业",
        "仓储/物流",
        "家政/月嫂",
        "航空",
        "教育/培训",
        "咨询/顾问",
        "学术/科研",
        "法律",
        "设计/创意",
        "文学/媒体",
        "餐饮/旅游",
        "能源/化工",
        "地质勘探",
        "医疗/护理",
        "保健/美容",
        "生物工程",
        "制药/器械",
        "体育工作者",
        "翻译",
        "公务员/干部",
        "个体/电商",
        "警察",
        "交通",
        "其他"
)

val maritalStatusList = arrayListOf("未婚", "已婚", "离异", "丧偶")
val educationList = arrayListOf("初中及以下", "高中及中专", "大专", "本科", "硕士及以上")
val monthlyIncomeList = arrayListOf("2000以下", "2000-4000", "4000-6000", "6000-10000", "10000-20000", "20000以上")
