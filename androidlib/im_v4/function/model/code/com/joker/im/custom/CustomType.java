package com.joker.im.custom;

import android.text.TextUtils;

import com.joker.im.custom.chick.ChickCustomData;


/**
 * 佛祖保佑         永无BUG
 * <p>
 * Created by joker on 2017/2/17.
 */

public enum CustomType {
    general(GenericData.Result.class),//通用组件
    general_special(GenericData.Result.class),//通用活动消息（图片+文本+高亮+跳转）
    im_share(GenericData.Result.class),//app内分享
    system_tip(SystemTipData.Result.class),//拓展系统通知
    pay_result(PayResultData.Result.class),//支付凭证
    course(CourseData.Result.class),//课程消息
    course_change(CourseChangeData.Result.class),//课程修改消息
    course_push(GenericData.Result.class),//app内分享
    group_system_notification(GroupSystemData.Result.class),//群通知消息
    invalid(NoneData.Result.class),//暂未支持
    apply_exclusive_timeout(ChickCustomData.Result.class),//私密连线申请超时
    apply_exclusive_result(ChickCustomData.Result.class),//私密连线申请超时 或者拒绝
    content_with_two_button(ContentWithTwoButtonData.Result.class),//两个按钮的消息类型
    content_with_long_button(ContentWithLongButtonData.Result.class),//一个长按钮的消息类型
    /**
     * 本地消息，不发送的
     **/
    report_order(ReportOrderData.Result.class),//举报私单
    course_recommend(GenericData.Result.class),//课程咨询推荐
    /****************************以下为Chick项目中自定义的消息************************************/
    /**
     * chick本地消息，不发送的
     */
    system_notify(ChickCustomData.Result.class),//系统提示消息
    im_send_gift(ChickCustomData.Result.class),//im送礼
    notify_mic_state(ChickCustomData.Result.class),//通知闭麦状态
    notify_video_convert_audio(ChickCustomData.Result.class),


    notify(ChickCustomData.Result.class),//纯文本消息
    send_gift(ChickCustomData.Result.class),//直播间送礼
    room_enter_anim(ChickCustomData.Result.class),//直播间入场动画
    send_text(ChickCustomData.Result.class),//直播间发言
    invite_up(ChickCustomData.Result.class),//邀请连线
    apply_upstream_result(ChickCustomData.Result.class),//同意连线
    invite_join(ChickCustomData.Result.class),//相亲邀请
    invite_friend_result(ChickCustomData.Result.class),//收到加好友申请
    invite_send_gift(ChickCustomData.Result.class),//直播间引导送礼
    toast(ChickCustomData.Result.class),//全局toast
    room_transform(ChickCustomData.Result.class),//转房申请
    room_end(ChickCustomData.Result.class),//直播结束，红娘主动退出结束相亲
    people_changed(ChickCustomData.Result.class),//申请队列修改  reason 1:排麦队列变化 2 人员进出 3 上麦 4 下麦
    room_broadcast(ChickCustomData.Result.class),//直播间广播
    room_reject_up(ChickCustomData.Result.class),//直播间用户拒绝连线
    room_type_change(ChickCustomData.Result.class),//直播间类型变更
    action_notify(ChickCustomData.Result.class),//直播间操作通知
    exclusive_room_ready(ChickCustomData.Result.class),//专属房开始去显示计时器
    apply_warning(ChickCustomData.Result.class),//排麦提醒
    apply_upstream(ChickCustomData.Result.class),//当前无人申请连线,队列没人排麦,此时申请弹发送此消息
    room_info_change(ChickCustomData.Result.class),//房间重新开启时 刷新room/info接口
    user_balance_changed(ChickCustomData.Result.class),//余额变化时通知客户端
    publisher_coin_changed(ChickCustomData.Result.class),//主播收到的心币总数变化
    mute_remote_stream(ChickCustomData.Result.class),//关闭远端音频流
    user_upstream(ChickCustomData.Result.class),//用户已经上麦

    call_status_changed(ChickCustomData.Result.class),//专属连线通话状态
    apply_exclusive(ChickCustomData.Result.class),//在私聊界面发出私密连线申请

    join_group(ChickCustomData.Result.class),//新用户进入直播间消息
    quit_group(ChickCustomData.Result.class),//退出直播间消息
    auto_kick_offline(ChickCustomData.Result.class),//远程抱用户下麦
    change_audio_state(ChickCustomData.Result.class),//关闭/打开连线用户的声音
    delete_message(ChickCustomData.Result.class),//远程删除消息
    room_mode_change(ChickCustomData.Result.class),//房间窗口模式变更
    user_level_change(ChickCustomData.Result.class),//用户等级变更
    room_apply_switch_change(ChickCustomData.Result.class),//房间申请连线状态变更
    room_forbidden(ChickCustomData.Result.class),//房间禁言操作
    special_service_card(ChickCustomData.Result.class),//主播推荐专属服务ID
    course_card(ChickCustomData.Result.class), // 课程卡片类型
    cancel_special_service(ChickCustomData.Result.class),//用户取消专属服务
    upload_local_log(ChickCustomData.Result.class),//声网日志
    exclusive_system_notify(ChickCustomData.Result.class),//拉黑通知
    week_rank_changed(ChickCustomData.Result.class), //周榜排名变化
    user_info_change(ChickCustomData.Result.class),//房管变化
    coupon(ChickCustomData.Result.class),//优惠券
    honor_medal(ChickCustomData.Result.class), //全局勋章升级，主播奖杯获取通知
    quick_apply_exclusive(ChickCustomData.Result.class), //快速咨询申请
    deduct_change(ChickCustomData.Result.class), //优惠券扣费转余额扣费
    general_card(ChickCustomData.Result.class),//测评报告
    exclusive_block_notify(ChickCustomData.Result.class), //拉黑通知
    reject_quick_apply_exclusive(ChickCustomData.Result.class), //快速咨询主播拒绝连线消息c2c
    general_card_popup(ChickCustomData.Result.class); //送课程


    private Class<?> cls;

    CustomType(Class<?> cls) {
        this.cls = cls;
    }

    public Class<?> getCls() {
        return cls;
    }

    public static CustomType parseValue(String type) {
        CustomType result = invalid;
        for (CustomType value : CustomType.values()) {
            if (TextUtils.equals(value.name(), type)) {
                result = value;
                break;
            }
        }
        return result;
    }

}
