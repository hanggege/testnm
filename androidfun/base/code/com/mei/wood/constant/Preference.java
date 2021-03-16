package com.mei.wood.constant;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/5.
 */

public interface Preference {
    String LAST_LOGIN_COUNTRY_CODE = "last_login_country_code";
    String LAST_LOGIN_ACCOUNT = "last_login_account";
    String DELAY_INPUT_PWD = "delay_input_password";//保存输入口令的上次时间
    String PREF_KEY_TAB_INDEX_NAME = "tabIndexName";
    String MENTOR_KEY_TAB_INDEX = "mentortabIndex";
    String aman_from_id = "aman_from_id";//干货来源
    String aman_survey_msg_id = "aman_survey_msg_id";//推送ID
    String aman_cate_name = "aman_cate_name";//分类名
    String aman_title_name = "aman_title_name";//标题
    String aman_pro_cate_name = "aman_pro_cate_name";//品类名
    String FIRST_COLLECT = "first_collect";//首次收藏干货
    String ROOM_ENTER = "ROOM_ENTER"; //直播间结束通知
    String ROOM_RED_PACKET_CLICK = "ROOM_RED_PACKET_CLICK"; //直播间红包点击
}
