package com.mei.wood.web;

import com.google.gson.annotations.SerializedName;

/**
 * Created by hang on 2019-07-03.
 * 智齿后台自定义字段对象
 */
public class CustomerParams {
    public CustomerParams(String name, int UID, String age, String phone, String sex) {
        this.name = name;
        this.UID = UID;
        this.age = age;
        this.phone = phone;
        this.sex = sex;
    }

    @SerializedName("昵称")
    public String name;
    int UID;
    @SerializedName("年龄")
    public String age;
    @SerializedName("手机号码")
    public String phone;
    @SerializedName("性别")
    public String sex;
}
