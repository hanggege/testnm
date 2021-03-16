package com.net.model.room;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.UserInfoWeekRank;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/9/9
 */
public class RoomWeekRank {
    @Nullable
    public ApplyOptionDtoItem applyOptionDtoItem;

    @Nullable
    public String rankTips;  //最后一个item显示内容

    @Nullable
    public RoomWeekRankList rank;
    @Nullable
    public String rankTitle;
    @Nullable
    public String weekRankHint;

    @Nullable
    public CurrentPublisher currentPublisher;

    public static class Response extends BaseResponse<RoomWeekRank> {

    }

    public class ApplyOptionDtoItem {
        @Nullable
        public String applyType;  //操作类型（连线，专属服务列表等）
        public int costType;
        public long id;
        public double price;
        @Nullable
        public String roomType;
        public boolean selected;
        @Nullable
        public String title;

    }

    public class CurrentPublisher {
        public List<SplitText> coinCount;
        public int publisherId;
        @Nullable
        public UserInfoWeekRank publisherInfo;
        public String rank;
    }
}
