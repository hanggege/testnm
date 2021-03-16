package com.net.model.room;

import androidx.annotation.Nullable;

import com.joker.im.custom.chick.ServiceInfo;
import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-29
 */

public class AgoraStatus {
    public static class Response extends BaseResponse<AgoraStatus> {

    }

    public int after_call;


    public RoomInfoChange roomInfoChange;

    public static class RoomInfoChange {
        public String title;
        public String tag;
        public long broadcastId;
        public String roomType;
        public int mode;
        public int videoMode;
        public List<Integer> allowUsers;
        public boolean applyUpstreamEnable;
        public ServiceInfo serviceInfo;
        @Nullable
        public RoomInfo.CreateUser createUser;
        @Nullable
        public RoomInfo.UpstreamCouponItem upstreamCouponItem; //当前房间是有用户在使用连线券连线
    }
}
