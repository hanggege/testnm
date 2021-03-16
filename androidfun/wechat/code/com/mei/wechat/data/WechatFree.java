package com.mei.wechat.data;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/13
 */
public class WechatFree {
    //"wechat_name":"xxx"                                //公众号
    //"reply_num":777                                        //回复数字
    //"report_source":"每日一测"                      //弹窗来源

    public String wechat_name;
    public int reply_num;
    public String report_source;

    public WechatFree(String wechat_name, int reply_num, String report_source) {
        this.wechat_name = wechat_name;
        this.reply_num = reply_num;
        this.report_source = report_source;
    }
}
