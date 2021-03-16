package com.net.model.chick.video;

import androidx.annotation.Nullable;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.room.RoomList;

import java.util.List;

/**
 * LikeVideoList
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-08-18
 */
public class LikeVideoList {

    public static class Response extends BaseResponse<LikeVideoList> {

    }

    public boolean hasMore;

    public int nextPageNo;

    @Nullable
    public String noContentTipsImage;

    @Nullable
    public String noContentTipsText;

    public List<RoomList.ShortVideoItemBean> likeList;
}
