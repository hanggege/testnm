package com.net.model.chick.friends;

import com.net.model.user.GroupInfo;
import com.net.model.user.Medal;
import com.net.model.user.UserInfo;

import java.util.Map;

import androidx.annotation.Nullable;

public class SubListBean extends UserInfo {

    public String roomId;
    /**
     * 房间类型 COMMON_ROOM-普通房间 EXCLUSIVE_ROOM-专属房间
     */
    public String roomType;
    /**
     * 在线状态 true-在线
     */
    public Boolean isLiving;


}