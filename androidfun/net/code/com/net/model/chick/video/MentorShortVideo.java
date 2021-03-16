package com.net.model.chick.video;

import com.mei.orc.http.response.BaseResponse;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/8/20
 */
public class MentorShortVideo {
    public MentorVideoInfo info;

    public static class Response extends BaseResponse<MentorShortVideo> {

    }

    public class MentorVideoInfo {
        @Nullable
        public List<Tag> tags;
        @Nullable
        public List<ShortVideoInfo.VideoInfo> videos;
        public boolean hasMore;
        public int nextPageNo;

    }

    public class Tag {
        public String action;
        public String name;
        public int seqId;
    }

}
