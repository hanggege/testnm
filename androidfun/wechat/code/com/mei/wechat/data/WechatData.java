package com.mei.wechat.data;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/11/9
 * 带上报
 */
public class WechatData {
    public int mentor_id;
    public String from_type;
    public String from_id;

    public String growing_key;

    public int emotion_help = 0;
    public int after_get_weixin = 1;

    public WechatData(int mentor_id, String from_type, String from_id) {
        this.mentor_id = mentor_id;
        this.from_type = from_type;
        this.from_id = from_id;
    }
}
