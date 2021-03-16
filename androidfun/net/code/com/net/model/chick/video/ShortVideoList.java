package com.net.model.chick.video;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.room.RoomList;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/11
 */


public class ShortVideoList {

    public ShortVideoItem videos;
    public List<VideoLabel> tags;

    public static class Response extends BaseResponse<ShortVideoList> {

    }

    public static class VideoLabel {
        public int tagId;
        public String name;
    }

    public static class ShortVideoItem {
        public List<RoomList.ShortVideoItemBean> list;
        public boolean hasMore;
        public int nextPageNo;
    }
}
