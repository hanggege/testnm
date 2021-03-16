package com.joker.model;


import com.joker.flag.TdFlags;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public class BackResult {
    @TdFlags
    public int type;
    public String open_id;
    public String token;
    public String code;//weixin专用
    //    public String access_token;//weixin专用
//    public String unionid;//weixin专用
    public Object qqToken;//qq专用于查用户信息

    public BackResult(@TdFlags int type) {//用于分享
        this.type = type;
    }

    public BackResult(int type, String code) {
        this.type = type;
        this.code = code;
    }

    public BackResult(@TdFlags int type, String open_id, String token) {
        this.type = type;
        this.open_id = open_id;
        this.token = token;
    }

//    public BackResult(@TdFlags int  type, String open_id, String token, String access_token,String unionid) {
//        this.type = type;
//        this.open_id = open_id;
//        this.token = token;
//        this.access_token = access_token;
//        this.unionid = unionid;
//    }

    public BackResult(@TdFlags int type, String open_id, String token, Object qqToken) {
        this.type = type;
        this.open_id = open_id;
        this.token = token;
        this.qqToken = qqToken;
    }

    @Override
    public String toString() {
        return "BackResult{" +
                "type=" + type +
                ", open_id='" + open_id + '\'' +
                ", token='" + token + '\'' +
                ", code='" + code + '\'' +
                ", qqToken=" + qqToken +
                '}';
    }
}
