package com.joker;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/27.
 */

public interface TdType {
    int qq = 1 << 10;
    int weibo = 1 << 11;
    int weixin = 1 << 12;

    //share type
    int image_share = 1 << 21;
    int multi_share = 1 << 22;
    int mini_program_share = 1 << 23;//只有微信有


    //client type
    int we_chat = 1 << 25;//好友
    int friend_circle = 1 << 26;//朋友圈或者qq空间

}
