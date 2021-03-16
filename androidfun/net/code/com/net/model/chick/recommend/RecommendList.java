package com.net.model.chick.recommend;

import com.mei.orc.http.response.BaseResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * author : Song Jian
 * date   : 2020/1/8
 * desc   :
 */
public class RecommendList {
    public static class Response extends BaseResponse<RecommendList> {

    }

    /**
     * size : int 查询记录数，默认10条
     * nextTime : date 第二次查询的时间，会在上一查询时次返回
     * list : [{"userId":"int 用户id","age":"int 年龄","avatar":"string 头像","gender":"int 性别 ( 0女 1男 -1未填写)","nickname":"string 昵称","location":"string 所在地","height":"Short 身高","hometown":"string 家乡","maritalStatus":"string 婚姻状况","makeFriendsHeart":"string 交友心声","online":"boolean 状态 true-","isLike":"boolean 喜欢 true-喜欢","roomId":"string 房间id","roomType":"int 相亲房间类型，只有消息类型是好友相亲时才有值，0-普通相亲房 1-专属相亲房"}]
     */

    public String size;
    public String nextTime;
    public List<ListBean> list;


    public static class ListBean {
        /**
         * userId : int 用户id
         * age : int 年龄
         * avatar : string 头像
         * gender : int 性别 ( 0女 1男 -1未填写)
         * nickname : string 昵称
         * location : string 所在地
         * height : Short 身高
         * hometown : string 家乡
         * maritalStatus : string 婚姻状况
         * makeFriendsHeart : string 交友心声
         * online : boolean 在线状态 true-在线
         * isLike : boolean 喜欢 true-喜欢
         * roomId : string 房间id
         * roomType : int 相亲房间类型，只有消息类型是好友相亲时才有值，0-普通相亲房 1-专属相亲房
         */

        public int userId;
        public int age;
        public String avatar;
        public int gender;
        public String nickname;
        public String location = "";
        public int height;
        public String hometown = "";
        public String maritalStatus;
        public String makeFriendsHeart;
        public boolean online;
        public boolean isLike;
        public String roomId;
        public String roomType = "";
        public int like;
        public int ageBegin;
        public int ageEnd;

        //按照优先级把个人信息加入到list
        private List<String> infoPriorityList = new ArrayList<>();

        public List<String> getUserInfoPriority() {
            //年龄、所在地、身高、家乡最多四项
            infoPriorityList.clear();
            if (age > 0) {
                infoPriorityList.add(age + "岁");
            }

            location = filterProvince(location);
            infoPriorityList.add(location);

            if (height > 0) {
                infoPriorityList.add(height + "cm");
            }

            hometown = filterProvince(hometown);
            if (hometown.length() > 0) {
                infoPriorityList.add("家乡:" + hometown);
            }
            return infoPriorityList;
        }

        private String filterProvince(String location) {
            //省份只显示前三个字
            if (location.contains("黑龙江") || location.contains("内蒙古")) {
                location = location.substring(0, 3);
            } else if (location.length() > 2) {
                location = location.substring(0, 2);
            }
            return location;
        }

        public String liveStatus() {
            if ("COMMON".equals(roomType)) {
                return "相亲中";
            } else if ("EXCLUSIVE".equals(roomType)) {
                return "专属相亲";
            } else {
                return "未相亲";
            }
        }
    }
}
