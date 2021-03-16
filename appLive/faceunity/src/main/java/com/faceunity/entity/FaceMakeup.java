package com.faceunity.entity;

import java.util.List;

/**
 * 轻美妆妆容组合
 *
 * @author Richie on 2018.11.15
 */
public class FaceMakeup {
    // 无妆
    public static final int FACE_MAKEUP_TYPE_NONE = -1;
    // 口红
    public static final int FACE_MAKEUP_TYPE_LIPSTICK = 0;
    // 腮红
    public static final int FACE_MAKEUP_TYPE_BLUSHER = 1;
    // 眉毛
    public static final int FACE_MAKEUP_TYPE_EYEBROW = 2;
    // 眼影
    public static final int FACE_MAKEUP_TYPE_EYE_SHADOW = 3;
    // 眼线
    public static final int FACE_MAKEUP_TYPE_EYE_LINER = 4;
    // 睫毛
    public static final int FACE_MAKEUP_TYPE_EYELASH = 5;
    // 美瞳
    public static final int FACE_MAKEUP_TYPE_EYE_PUPIL = 6;

    private List<MakeupItem> mMakeupItems;
    private int nameId;
    private int iconId;

    public FaceMakeup(List<MakeupItem> makeupItems, int nameId, int iconId) {
        mMakeupItems = makeupItems;
        this.nameId = nameId;
        this.iconId = iconId;
    }

    public List<MakeupItem> getMakeupItems() {
        return mMakeupItems;
    }
}
