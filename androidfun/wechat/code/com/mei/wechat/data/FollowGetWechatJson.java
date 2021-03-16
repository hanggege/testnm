package com.mei.wechat.data;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2018/6/7
 */
public class FollowGetWechatJson {

    public int is_follow;//int，必须，用户触发的动作，1-关注(app需要弹微信框) 0-取消关注
    public String tips;//"添加导师微信，免费咨询情感问题", // String，可选，微信框的说明文案
    public String avatar;// String，可选，导师头像
    public String wechat_id;// String，可选，导师微信id
    public int mentor_id;
    public String growing_key;

    public FollowGetWechatJson(int is_follow, String avatar, String wechat_id, int mentor_user_id) {
        this.is_follow = is_follow;
        this.tips = "";
        this.avatar = avatar;
        this.wechat_id = wechat_id;
        this.mentor_id = mentor_user_id;
    }
}