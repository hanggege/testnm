package com.net.model.room;

import androidx.annotation.NonNull;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

/**
 * Created by zhanyinghui on 2020/4/13
 * Des:
 */
public class ContributionsInfo {
    public ContributionListBean rank;
    /**
     * 用户还差多少升级到下一级
     */
    public UserRank userRank;
    /**
     * 总的贡献值
     */
    public long coinCount;

    public static class Response extends BaseResponse<ContributionsInfo> {

    }

    /***
     * 贡献值列表Bean
     */
    public class ContributionListBean {
        public boolean hasMore;
        public List<ContributionBean> list;
        public int nextPageNo;

    }

    /**
     * 用户还差多少升级到下一等级
     */
    public class UserRank {
        public int needSend;
        public int rank;
        public long sendCount;

        @NonNull
        @Override
        public String toString() {
            return needSend + "," + rank + "," + sendCount;
        }
    }

    public class ContributionBean {
        public long coinCount;
        public long userId;
        public UserInfo userInfo;

    }

    public class UserInfo {
        public String avatar;
        public int gender;
        public String nickname;
        public int userId;
        public int userLevel;
        public boolean isPublisher;

    }
}
