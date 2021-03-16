package com.net.model.chick.friends;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * Created by zzw on 2019-12-11
 * Des:
 */
public class MyFollowListBean {

    public static class Response extends BaseResponse<List<MyFollowListBean>> {

    }


    /**
     * avatar :
     * gender : 1
     * nickname : 未知
     * online : false
     * roomInfo : {"begin":1582701751307,"roomId":"213311","roomType":"COMMON_ROOM","userId":26666666}
     * userId : 0
     */

    public String avatar;
    public int gender;
    public String nickname;
    public boolean online;
    public RoomInfoBean roomInfo;
    public int userId;
    public boolean isGroup;
    /**
     * 形象照
     */
    @Nullable
    public String personalImage;

    /**
     * 形象照底色
     */
    public String backgroundColor;


    public static class RoomInfoBean {
        /**
         * begin : 1582701751307
         * roomId : 213311
         * roomType : COMMON_ROOM
         * userId : 26666666
         */

        public long begin;
        public String roomId;
        public String roomType;
        public int userId;

    }


}
