package com.joker.model;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2017/4/28.
 */

public class TdUserInfo {

    public String avatar;
    public int gender;//用户性别，1为男性，2为女性
    public String nick_name;
    public String location;

    public boolean isMan() {
        return gender == 1;
    }

}
