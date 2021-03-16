package com.net.model.chick.report;

import com.joker.im.custom.chick.ServiceInfo;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.Medal;

import java.util.List;
import java.util.Map;

public class ChatC2CUseInfo {


    public static class Response extends BaseResponse<ChatC2CUseInfo> {

    }

    public List<ServiceInfo> mySpecialServices;
    public InfoBean info;

    public static class InfoBean {
        /**
         * avatar : https://img.meidongli.net/28619696/2020/05/12/a014f4247116cdc08e6ac8b945ef88a1.jpg
         * birthYear : 95后
         * gender : 0
         * interests : [{"id":1,"name":"恋爱心理"}]
         * isPublisher : false
         * isRoomOwner : false
         * isSelf : false
         * isTest : true
         * nickname : 匿名后咯哦哦加了
         * roleId : 0
         * sendCoinCount : 1702936
         * sendMeCount : 0
         * status : 0
         * upstreamCount : 34
         * userId : 28619696
         * userLevel : 20
         */

        private String avatar;
        private String birthYear;
        private int gender;
        private boolean isPublisher;
        private boolean isRoomOwner;
        private boolean isSelf;
        private boolean isTest;
        private String nickname;
        private String roleId;
        private int sendCoinCount;
        private int sendMeCount;
        private int status;
        private int upstreamCount;
        private int userId;
        public int onlineStatus; //2-在线 0-离线
        private int userLevel;
        private List<InterestsBean> interests;
        public Map<String, Medal> medalMap;

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getBirthYear() {
            return birthYear;
        }

        public void setBirthYear(String birthYear) {
            this.birthYear = birthYear;
        }

        public int getGender() {
            return gender;
        }

        public void setGender(int gender) {
            this.gender = gender;
        }

        public boolean isIsPublisher() {
            return isPublisher;
        }

        public void setIsPublisher(boolean isPublisher) {
            this.isPublisher = isPublisher;
        }

        public boolean isIsRoomOwner() {
            return isRoomOwner;
        }

        public void setIsRoomOwner(boolean isRoomOwner) {
            this.isRoomOwner = isRoomOwner;
        }

        public boolean isIsSelf() {
            return isSelf;
        }

        public void setIsSelf(boolean isSelf) {
            this.isSelf = isSelf;
        }

        public boolean isIsTest() {
            return isTest;
        }

        public void setIsTest(boolean isTest) {
            this.isTest = isTest;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getRoleId() {
            return roleId;
        }

        public void setRoleId(String roleId) {
            this.roleId = roleId;
        }

        public int getSendCoinCount() {
            return sendCoinCount;
        }

        public void setSendCoinCount(int sendCoinCount) {
            this.sendCoinCount = sendCoinCount;
        }

        public int getSendMeCount() {
            return sendMeCount;
        }

        public void setSendMeCount(int sendMeCount) {
            this.sendMeCount = sendMeCount;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUpstreamCount() {
            return upstreamCount;
        }

        public void setUpstreamCount(int upstreamCount) {
            this.upstreamCount = upstreamCount;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public int getUserLevel() {
            return userLevel;
        }

        public void setUserLevel(int userLevel) {
            this.userLevel = userLevel;
        }

        public List<InterestsBean> getInterests() {
            return interests;
        }

        public void setInterests(List<InterestsBean> interests) {
            this.interests = interests;
        }

        public static class InterestsBean {
            /**
             * id : 1
             * name : 恋爱心理
             */

            private int id;
            private String name;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }
        }
    }
}
