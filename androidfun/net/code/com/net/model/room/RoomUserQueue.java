package com.net.model.room;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.Medal;
import com.net.model.user.UserInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by hang on 2020/8/17.
 */
public class RoomUserQueue {

    public static class Response extends BaseResponse<RoomUserQueue> {
    }

    /**
     * tabs
     */
    public List<RoomUserTabItem> tabs;

    /**
     * 用户列表
     */
    public List<RoomUserQueueItem> userItems;

    public boolean hasMore;

    public int nextPageNo;

    public InviteOption applyOption;

    public static class RoomUserTabItem {

        /**
         * tab显示文案
         */
        public String tabName;

        /**
         * tab类型
         */
        public String tabTypeName;

        /**
         * 是否有上麦申请开关
         */
        public boolean hasApplySwitch = false;

        public String emptyTips;
    }

    public static class InviteOption {
        public String applyType;
        public String costTips;
        public String roomType;
        public String title;
    }

    public static class RoomUserQueueItem {

        /**
         * 用户信息
         */
        public UserInfo user;

        /**
         * 标题
         */
        public SplitText title;

        /**
         * 副标题
         */
        public List<SplitText> subTitle;

        /**
         * 是否在线
         */
        public boolean online;

        /**
         * 操作
         */
        public String btnText;

        public String roomType;
        public Map<String, Medal> medalMap;//服务标识
        public int remainServiceDuration;
        public boolean free;
        public boolean lessThanFiveMin;
        private String status = "";

        public RoomApplyState getState() {
            return RoomApplyState.parseValue(status);
        }

        public void setState(RoomApplyState state) {
            status = state.name();
        }
    }
}
