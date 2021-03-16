package com.mei.wood.ext;

import com.mei.wood.BuildConfig;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/5/5.
 */

public interface AmanLink {
    String prefix = BuildConfig.APP_SCHEME;

    interface URL {

        String USER_CHAT_PATTERN = prefix + "://user_chat/(\\d+)";
        String USER_CHAT_SEND_MESSAGE = prefix + "://user_chat_send_message";
        String IM_CHAT_C2C = prefix + "://c2c_chat";//新的去IM内链 dove://c2c_chat?user_id=xxx
        String USER_LOGIN = prefix + "://user_login";
        String USER_LOGIN_NEW = prefix + "://user_login_new";
        String USER_LOGIN_CHECK = prefix + "://user_login_check";
        String GO_TO_LOGIN_DIRECTLY = prefix + "://go_to_login_directly";//直接打开登录页面
        String COPY_WORDS = prefix + "://copy_words/(.*)";
        String EXIT_WEBVIEW = prefix + "://exit_webview/(.*)";
        String NEW_WEBVIEW = prefix + "://new_webview/(.*)";
        String NEW_JSON_WEBVIEW = prefix + "://new_json_webview/(.*)";
        String CANCELFOLLOWMENTOR_FROMWEB = prefix + "://cancelfollowmentor_fromweb/(\\d+)";             //取消关注一个导师--来自web
        String FOLLOWMENTOR_FROMWEB = prefix + "://followmentor_fromweb/(\\d+)";                //关注一个导师--来自web
        String OPEN_SYSTEM_NET_CLIENT = prefix + "://open_system_net_client/(.*)";  //打开系统浏览器


        String COURSE_SPEC_ORDER = prefix + "://course_spec_order/([a-z0-9A-Z]+)/(\\d+)/(\\d+)/(\\d+)";//xiaolu2.0://course_spec_order/$refertype/$referid/$specid/$promoteruserid
        String close_webview = prefix + "://close_webview";//关闭web页面
        String webview_message_channel = prefix + "://webview_message_channel/(.*)";////web自己进行通信
        String show_toast = prefix + "://show_toast/(.*)";//toast
        String show_loading = prefix + "://show_loading/(\\d+)";//显示或隐藏loading框
        String web_current_index = prefix + "://web_current_index/(\\d+)";
        String APP_GET_WEB_TITLE = prefix + "://app_get_web_title/(.*)";
        String HIDE_TITLE_LINE = prefix + "://hide_title_line/(\\d+)";
        String SHARE_FROM_WEB = prefix + "://share_from_web/(.*)";// xiaolu2.0://share_from_web/&json  web分享到app中
        String SHARE_TARGET_WEB = prefix + "://share_target_web/(.*)";// xiaolu2.0://share_target_web/&json  web分享到指定位置，不需要调起分享弹框
        String CREATE_NEW_WEB = prefix + "://create_new_web/(.*)";// xiaolu2.0://create_new_web/&json （{need_login: 0 or 1 ，url : 加载url}）
        String PHOTO_GALLERY = prefix + "://photo_gallery/(.*)";//wap通用查看大图json
        String WEBVIEW_EDIT_CONTENT = prefix + "://webview_edit_content/(\\d+)";//webview页面是否有编辑内容，通知原生。0表示空内容。1表示有内容
        String WEBVIEW_SHOW_TOOLBAR_REFRESH_ICON = prefix + "://webview_show_toolbar_refresh_icon/(\\d+)";//webview页面是否有编辑内容，通知原生。0表示保留。1表示隐藏
        String WECHAT_ADD = prefix + "://add_wechat_friend/(.*)";  // 微信添加,$mentor_user_id/$from_type/$from_id  修改正则  兼容数据
        //start课程相关 新规则内链 已key -value的形式
        /***
         * web页面控制播放内链
         * 内链中会携带播放音频所需要的数据 参数是：playerStatus=（1，0） 1  播放，0暂停
         * id 音频id
         * audioInfo 里的结构和
         * @see com.pili.PlayInfo 一样，这里有必须要的数据（需要我们重新设置 PlayType）
         */
        String COURSE_SERVICE_PLAYER = prefix + "://service_course_player";
        String COURSE_SERVICE_WEBVIEW = prefix + "://course_service_webView"; //课程服务专用web页面
        String HALF_SCREEN_COURSE_SERVICE_WEBVIEW = prefix + "://half_screen_course_service_webView"; //直播间半屏课程服务专用web页面
        String COURSE_SERVICE_JSON_WEBVIEW = prefix + "://course_service_json_webView"; //课程服务专用web页面 json 数据
        String COURSE_PAY_DIALOG = prefix + "://course_service_dialog"; //课程相关的支付充值弹框
        String AUDIO_PLAYER = prefix + "://audio_player"; //跳转到播放器内链需要提供audioId
        String COURSE_PAY_SUCCESS = prefix + "://course_pay_success";//  课程充值成功后web回调原生通知
        String LOAD_FINISH = prefix + "://load_finish"; //web 会根据页面加载完成和数据加载完成给参数（type 1 页面，2 数据请求）
        //end课程相关


        String SHORT_VIDEO_PLAYER = prefix + "://short_video_player";  //短视频详情
        String SQUARE_RECOMMEND_PAGE = prefix + "://square_recommend_page";  // 广场推荐页 参数（tabId&tagId）

        //勋章
        String MEDAL_DIALOG = prefix + "://medal_dialog";


        // 通用弹框
        String BASIC_ALERT_SHOW = prefix + "://basic_alert_show/(.*)"; // 尾部参数是以json结构体
        String CHANGE_NAV_TITLE = prefix + "://change_nav_title/(.*)";
        String WEB_CALL_APP_PAY_SDK = prefix + "://app_pay_sdk/(.*)";//网页调起本地支付sdk
        String WEB_CALL_APP_PAY_SDK_V2 = prefix + "://app_pay_sdk_v2/(.*)";//新网页调起本地支付sdk
        String show_pay_dialog = prefix + "://show_pay_dialog";//打开支付弹框

        String save_base64_image = prefix + "://save_image/(.*)"; // 保存base64编码的字符串图片
        //情感测评
        String has_finished_emotion_test = prefix + "://has_finished_emotion_test";//测试完成
        String back_show_alert = prefix + "://back_show_alert/(.*)";//点击返回弹框json 格式 {"title":“确定退出测试吗”,"confirm_text":"确定","cancel_text":"取消"}
        String goto_mini_program = prefix + "://goto_mini_program/(.*)";//跳转小程序
        String SHARE_WEBVIEW = prefix + "://share_webview/(.*)";//带有右上角分享按钮的webview，分享h5页面
        String HAS_FINISHED_LOAD_WEB = prefix + "://has_finished_load_web";//用于隐藏加载菊花
        String jump_to_wechat = prefix + "://jump_to_wechat";//跳转微信

        String jump_to_video_live = prefix + "://jump_to_video_live";//跳转直播页面
        String is_show_system_invite_join = prefix + "://is_show_system_invite_join";//当前页面是否展示系统邀请相亲弹窗
        String login_out = prefix + "://login_out";//退出登录
        String account_over = prefix + "://app_logout_success/1";//注销完成
        String account_back = prefix + "://app_logout_success/0";//注销完成 返回按钮
        String account_out_ok = prefix + "://app_logout_clear";//注销完成  用户点击了弹窗按钮
        String report_user = prefix + "://report_user";//举报
        String room_blacklist_delete = prefix + "://room_blacklist_delete";//取消拉黑
        String tab_select = prefix + "://tab_select?(.*)";//跳转到主页的tab  chick://tab_select?tabbar=my
        String jump_home = prefix + "://jump_home";//跳转 首页
        String jump_personal_page = prefix + "://jump_personal_page_info";//跳转 个人主页
        String jump_short_video_list_page = prefix + "://jump_short_video_list_page";//跳转短视频列表页
        String jump_message_notification_page = prefix + "://jump_message_notification_page";//跳转 个人主页
        String set_refresh_callback = prefix + "://set_refresh_callback/([a-z0-9A-Z]+)";
        String jump_short_video_square_page = prefix + "://jump_short_video_square_page";//跳转广场列表页

        String web_send_to_im = prefix + "://web_send_to_im/(.*)";//公屏消息
        String exclusive_service_ask_dialog = prefix + "://exclusive_service_ask_dialog";//服务弹窗
        String is_hand_show_webview = prefix + "://is_hand_show_webview";//控制弹窗是否关闭或者弹出
        //        dove://player_controller?state=0&showBar=0
//        state: 0 暂停 1 播放 -1 停止
//        showBar:0 隐藏 1显示
        String player_controller = prefix + "://player_controller";//控制弹窗是否关闭或者弹出

        String find_page_height = prefix + "://find_page_height";//发现页的课程列表
        String find_content_tab = prefix + "://find_content_tab";//发现页内部的tab切换

        String start_common_live = prefix + "://start_common_live";//开播界面
        String beauty_available = prefix + "://beauty_available";//美颜配置界面

        String apply_service = prefix + "://apply_service"; //申请服务连线
        String gio_track_web = prefix + "://gio_track_web/(.*)";//web通知gio埋点

        String coupon_dialog = prefix + "://coupon_dialog";//优惠券弹窗

        String jump_radio = prefix + "://jump_radio";//跳转到电台

        String snap_up_coupon = prefix + "://snap_up_coupon";//抢券
        String snap_up_consult = prefix + "://snap_up_consult";//抢券界面中的立即咨询


        String open_live_room_chat = prefix + "://open_live_room_chat"; //拉起直播间半屏聊天
        String to_receive_course = prefix + "://to_receive_course";//领取课程

        String new_people_gift_bag_dialog = prefix + "://new_people_gift_bag_dialog"; //新人礼包领取弹框
        String use_coupon_apply_upstream = prefix + "://use_coupon_apply_upstream";//使用优惠券申请上麦
    }


    interface NetUrl {
        String wallet_index = "https://zxl.meidongli.net/zhixin/index.html";//我的钱包界面
        String consume_record = "https://zxl.meidongli.net/zhixin/consume.html";//明细，支出&充值

        String chat_artifact_agreement = "https://zxl.meidongli.net/zhixin/agreement.html";//协议页面
        String privacy_page = "https://zxl.meidongli.net/zhixin/agreement.html";//用户隐私
        String about_url = "https://zxl.meidongli.net/zhixin/about.html";//关于
        String logout_url = "https://zxl.meidongli.net/zhixin/cancellation-account.html";//注销
        String living_invite_url = "http://zxl.meidongli.net/zhixin/room-invitation.html";//直播间邀请

        String article_info_url = "https://m.meidongli.net/elite/index_v13";//文章页面
        String xiaolu_about_url = "https://m.yeshouxiansheng.com/html/o/about-xl-new.html";//认识小鹿

        String exclusive_service_details = "https://zxl.meidongli.net/zhixin/exclusive-service-info2.html";//专属服务详情
        String exclusive_service = "https://zxl.meidongli.net/zhixin/exclusive-service2.html";//专属服务
        String exclusive_service_list = "https://zxl.meidongli.net/zhixin/exclusive-service-list2.html"; //专属服务列表

        String user_record = "https://zxl.meidongli.net/zhixin/user-record.html"; //需要增加type：fans，connection，exclusive
        String course_introduce = "https://zxl.meidongli.net/zhixin/course-service-info.html";//课程介绍
        String course_list_url = "https://zxl.meidongli.net/zhixin/course-service-list.html";//课程表
        String course_words_url = "https://zxl.meidongli.net/zhixin/course-words.html";//文稿

        String work_room_course = "https://zxl.meidongli.net/zhixin-workroom/workroom-course.html";
        String work_room_service = "https://zxl.meidongli.net/zhixin-workroom/workroom-service.html";
        String instructions = "http://sssr.meidongli.net/dove/v17/instructions";//直播间使用手册
    }


}
