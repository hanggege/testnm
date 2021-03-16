package com.joker.im.custom

/**
 * Created by 杨强彪 on 2017/4/20.
 * @描述：
 */
class CourseData : CustomBaseData() {
    class Result(type: CustomType, data: CourseData) : CustomInfo<CourseData>(type.name, data)

    var course_id: String = ""//课程CourseID
    var course_image_url: String = ""//图片url
    var link_url: String = ""//跳转内链
    var course_name: String = ""//课程名字
    var course_team_name: String = ""//团队名字
    var course_price: String = ""//课程价格
    var course_time: String = ""//课程截止时间
    var teacher_id: String = ""//导师ID
    var refer_type: String = ""//course类型
    var refer_id: String = ""//courseID
    var order_sn: String = ""//订单号
    var order_type: String = ""//订单类型
    var promoter_user_id: String = ""//推广员

    override val summary: String
        get() = course_name
    override val copySummary: String
        get() = course_name
}