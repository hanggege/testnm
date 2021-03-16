package com.net.model.room;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/11/16
 */

public class MatchInfo {

    public int publisherUserId;
    public int groupId;
    @Nullable
    public String roomId;
    @Nullable
    public String name;
    @Nullable
    public String avatar;
    public int weight;
    @Nullable
    public String publisherSkills;
    @Nullable
    public List<String> publisherSkillsSet;

    @Nullable
    public List<PublisherSkillsBean> publisherSkillList;
    @Nullable
    public String status;
    @Nullable
    public String roomType;

    public boolean onlyOne; //有没有更多

    public static class Response extends BaseResponse<MatchInfo> {
    }

    public class PublisherSkillsBean {
        /**
         * id : 3
         * name : 星座占卜
         */

        public int id;
        public String name;


    }
}
