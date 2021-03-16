package com.mei.faceunity.entity;

import com.mei.wood.R;

public enum MeiXingEnum {
    //美型
    Face_lift("facelift", R.id.beauty_box_cheek_thinning),//瘦脸
    FaceV_V("facev", R.id.beauty_box_cheek_v),//V脸
    Narrow_face("narrowface", R.id.beauty_box_cheek_narrow),//窄脸
    Small_face("smallface", R.id.beauty_box_cheek_small),//小脸
    big_eyes("bigeyes", R.id.beauty_box_eye_enlarge),//大眼
    chin("chin", R.id.beauty_box_intensity_chin),//下巴
    Forehead("orehead", R.id.beauty_box_intensity_forehead),//额头
    Thin_nose("thin_nose", R.id.beauty_box_intensity_nose),//瘦鼻
    Mouth_type("mouth_type", R.id.beauty_box_intensity_mouth);//嘴型

    private String filterName;
    private int resId;

    MeiXingEnum(String name, int resId) {
        this.filterName = name;
        this.resId = resId;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
