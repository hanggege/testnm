package com.mei.orc.john.model;

import android.text.TextUtils;

import com.mei.orc.sex.SexVersionManagerKt;
import com.mei.orc.util.string.NumberUtilKt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joker on 2018/3/19.
 */

public class CookieModel {

    public int gender_version;
    public List<Integer> user_interest;
    public int pro_cate_id;

    public CookieModel(int gender_version, List<Integer> user_interest, int pro_cate_id) {
        this.gender_version = gender_version;
        this.user_interest = user_interest;
        this.pro_cate_id = pro_cate_id;
    }

    public static CookieModel parseCookie() {
        List<Integer> user_interest = new ArrayList<>();
        String[] ids = TextUtils.split(SexVersionManagerKt.getInterest(), ",");
        for (String id : ids) {
            int value = NumberUtilKt.getInt(id, -1);
            if (value > -1) {
                user_interest.add(value);
            }
        }
        int cateId = SexVersionManagerKt.getProCateId();
        return new CookieModel(SexVersionManagerKt.getSexVersion().sexValue(), user_interest, cateId);
    }
}
