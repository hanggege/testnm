package com.mei.wechat.data;

/**
 * @author Created by hang on 2018/10/25
 */
public class IMWechatJson {

    public int mentor_id;
    public String from;
    public String growing_key;
    public int from_id;
    public int after_get_weixin;
    public int emotion_help;
    public String pro_cate_name;

    public WechatData createWechatData() {
        WechatData data = new WechatData(mentor_id, from, String.valueOf(from_id));
        data.after_get_weixin = after_get_weixin;
        data.emotion_help = emotion_help;
        data.growing_key = growing_key;
        return data;
    }


}