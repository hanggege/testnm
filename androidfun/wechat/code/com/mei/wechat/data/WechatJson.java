package com.mei.wechat.data;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/7
 * 公从号分享
 */
public class WechatJson {

    public String wechat_id;
    public String from_type;
    public String from_id;
    public String report_source;

    public WechatJson(String wechat_id, String from_id, String from_type, String report_source) {
        this.wechat_id = wechat_id;
        this.from_id = from_id;
        this.from_type = from_type;
        this.report_source = report_source;
    }
}