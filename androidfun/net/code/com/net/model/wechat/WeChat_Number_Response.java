package com.net.model.wechat;

import com.mei.orc.http.response.BaseResponse;

/**
 * Created by 杨强彪 on 2016/2/19.
 *
 * @描述：
 */
public class WeChat_Number_Response {
    public static class Response extends BaseResponse<WeChat_Number_Response> {

    }


    public String avatar;
    public boolean review_version;
    public String weixin_ab_ver;

    @Override
    public String toString() {
        return "WeChat_Number_Response{" +
                "avatar='" + avatar + '\'' +
                ", weixin_id='" + weixin_id + '\'' +
                '}';
    }

    public String weixin_id;

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getWeixin_id() {
        return weixin_id;
    }

    public void setWeixin_id(String weixin_id) {
        this.weixin_id = weixin_id;
    }


}
