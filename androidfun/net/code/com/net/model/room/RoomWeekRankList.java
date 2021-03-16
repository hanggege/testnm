package com.net.model.room;

import com.net.model.user.UserInfoWeekRank;

import java.util.ArrayList;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/9/9
 */
public class RoomWeekRankList {
    public boolean hasMore;
    public int nextPageNo;
    @Nullable
    public ArrayList<WeekRank> list;

    public class WeekRank {
        @Nullable
        public String coinCount;
        public long userId;
        @Nullable
        public UserInfoWeekRank userInfo;
        //排名
        public int no;

    }
}
