package com.joker.im.custom


/**
 * Created by 杨强彪 on 2017/4/20.
 * @描述：
 */
class CourseChangeData : CustomBaseData() {
    class Result(type: CustomType, data: CourseChangeData) : CustomInfo<CourseChangeData>(type.name, data)

    var change_info_name: String = ""//课程名字
    var change_info_course_id: String = ""//课程CourseID
    var link_url: String = ""//跳转内链
    var change_info_refer_id: String = ""//ReferID
    var change_info_time: String = ""//课程时间
    var change_info_remark: String = ""//备注
    var change_info_teacher_id: String = ""//导师ID
    var change_info_price: String = ""//修改后价格
    var change_info_type: String = ""//界面区别参数，1，修改价格，2，购买完成
    var change_info_refertype: String = ""//course类型
    var change_info_promoter_userid: String = ""//推广员

    override val summary: String
        get() = change_info_name
    override val copySummary: String
        get() = change_info_name
}