package com.mei.faceunity.entity;

import com.mei.wood.R;

public enum MeiFuEnum {
    //美肤
    PreciseBeauty("precisebeauty", R.id.beauty_box_skin_detect),//精准美颜
    Microdermabrasion("microdermabrasion", R.id.beauty_box_blur_level),//磨皮
    Whitening("whitening", R.id.beauty_box_color_level),//美白
    rosy("rosy", R.id.beauty_box_red_level),//红润
    Dazzling("dazzling", R.id.beauty_box_eye_bright),//亮眼
    beauty_box_tooth_whiten("beauty_box_tooth_whiten", R.id.beauty_box_tooth_whiten);//美牙


    private String filterName;
    private int resId;

    MeiFuEnum(String name, int resId) {
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
