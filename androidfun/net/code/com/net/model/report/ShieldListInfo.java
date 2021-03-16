package com.net.model.report;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.Medal;

import java.util.List;
import java.util.Map;

import androidx.annotation.Nullable;

/**
 * @ProjectName: dove
 * @Package: com.net.model.report
 * @ClassName: ShieldListInfo
 * @Description:
 * @Author: zxj
 * @CreateDate: 2020/6/16 15:51
 * @UpdateUser:
 * @UpdateDate: 2020/6/16 15:51
 * @UpdateRemark:
 * @Version:
 */
public class ShieldListInfo {
    public boolean hasMore;
    public int nextPageNo;
    public int pageSize;
    public List<ListBean> list;
    public int total;

    public static class Response extends BaseResponse<ShieldListInfo> {

    }

    public static class ListBean {

        public String createTime;
        public int optUserId;
        public String roomId;
        public int userId;
        public int targetId;
        @Nullable
        public UserInfoBean targetInfo;


        public static class UserInfoBean {
            @Nullable
            public String avatar;
            public String birthYear;
            public int gender;
            public boolean isTest;
            public Map<String, Medal> medalMap;
            public String nickname;
            public String roleId;
            public int groupId;
            public int status;
            public int userId;
            public int userLevel;
            public boolean isPublisher;

        }
    }
}
