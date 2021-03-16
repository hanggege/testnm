package com.net.model.room;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.Medal;

import java.util.List;
import java.util.Map;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2019-12-13
 */

public class QueueShow {
    public static class Response extends BaseResponse<QueueShow> {

    }

    public UserList userList;
    public InviteOption applyOption;
    public boolean canInviteUser;

    public static class UserList {
        public boolean hasMore;
        public List<ShowUser> list;
        public int nextPageNo;
    }


    public static class ShowUser {
        public String avatar;
        public boolean free;
        public int gender;
        public boolean lessThanFiveMin;
        public String nickname;
        public int remainServiceDuration;
        public String roleId;
        public String roomType;
        public long sendToMe;
        public long sendTotal;
        public boolean groupMember;
        public int userId;
        public int age;
        public int height;
        public String location;//家乡
        public Map<String, Medal> medalMap;//服务标识
        public boolean isPublisher;
        public List<SplitText> subTitle;
        /**
         * 申请状态
         */
        private String status = "";


        public RoomApplyState getState() {
            return RoomApplyState.parseValue(status);
        }

        public void setState(RoomApplyState state) {
            status = state.name();
        }

    }

}
