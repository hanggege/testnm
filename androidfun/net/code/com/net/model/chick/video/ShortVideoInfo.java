package com.net.model.chick.video;

import androidx.annotation.Nullable;

import com.joker.im.custom.chick.SplitText;
import com.mei.orc.http.response.BaseResponse;
import com.net.model.room.RoomInfo;

import java.util.List;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/11
 */


public class ShortVideoInfo {
    public VideoInfo current;
    public List<VideoInfo> last;
    public List<VideoInfo> next;
    @Nullable
    public String nextStartKey;

    public static class Response extends BaseResponse<ShortVideoInfo> {

    }

    public static class VideoInfo {

        public String detailUrl;
        public String guiding;
        public String seqId;
        public String shareUrl;
        public String title;
        public String image;
        public SplitText desc;
        public String action;
        public UserBean user;
        public int userId;
        public String videoCoverUrl;
        public String videoGifUrl;
        public String videoUrl;
        public int videoHeight;
        public int videoWidth;
        public int videoFormat = 0;//// m3u8 = 1, mp4 = 2, flv = 3
        public List<EntranceBean> entrance;
        public List<EntranceBean> entranceForPlaying;
        public boolean isGroup; //是否是工作室成员
        public boolean isPlay;
        public boolean living;
        public boolean like;
        public boolean subscribe;
        @Nullable
        public RoomInfo livingRoomInfo;
        @Nullable
        public List<SplitText> tagItems;
        @Nullable
        public String closeConfirmTips;
        /**
         * 是否重播
         */
        public boolean canAutoReplay;
        /**
         * 是否播放下一个
         */
        public boolean canAutoNext;

        /**
         * 是否显示点赞按钮
         */
        public boolean showLikeBtn = true;

        /**
         * 是否显示 作者信息
         */
        public boolean showPublisherInfo = true;

        /**
         * 是否显示标题
         */
        public boolean showTitle = true;

        /**
         * 是否显示标签
         */
        public boolean showTags = false;

        /**
         * 是否显示关注按钮
         */
        public boolean showFollowBtn = true;

        public static class UserBean {
            public String avatar;
            public int gender;
            public boolean isFocus;
            public String nickName;
            public int userId;
            /**
             * 形象照
             */
            @Nullable
            public String personalImage;

            /**
             * 形象照底色
             */
            public String backgroundColor;
        }

        public static class EntranceBean {
            public String action;
            public String icon;
            public String text;
            public String type;
        }
    }
}
