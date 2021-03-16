package com.net.model.chick.report;

import com.joker.im.custom.chick.ServiceInfo;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.RoomInfo;
import com.net.model.room.SpecialServiceBean;
import com.net.model.user.GroupInfo;
import com.net.model.user.UserInfo;

import java.util.List;

import androidx.annotation.Nullable;

public class ChatC2CPubInfo {
    public static class Response extends BaseResponse<ChatC2CPubInfo> {
    }

    public SpecialServiceBean specialServices;
    public int specialServicesCount;
    public List<ServiceInfo> mySpecialServices;
    public InfoBean info;

    public static class InfoBean {
        /**
         * homepageCover : https://img.meidongli.net/29408403/2020/05/14/Screenshot_2020-05-08-21-02-54-752_com.mei.goat.png
         * introduction : 哈哈哈哈,1,2,3,4,5,6,7,8,9
         * isFollow : false
         * onlineStatus : 0
         * publisherTags : ["哈哈哈哈","1","2","3","4","5","6","7","8","9"]
         * roomId : 313253
         * userId : 29408403
         * userInfo : {"avatar":"https://img.meidongli.net/2020/04/13/chick/user/avatar/17.png","birthYear":"70后","gender":0,"isTest":true,"nickname":"重情义的凉茶","roleId":"1","status":0,"userId":29408403,"userLevel":6}
         */

        public String homepageCover;
        public String introduction;
        public boolean isFollow;
        public int onlineStatus;
        public String roomId;
        public int userId;
        public UserInfo userInfo;
        @Nullable
        public RoomInfo roomInfo;
        @Nullable
        public String personalImage;
        public List<String> publisherTags;
        public List<UserInfo.InterestsBean> skills;

    }
}
