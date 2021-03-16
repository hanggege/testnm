package com.net.model.radio;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.user.UserInfo;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/9/23
 */
public class RadioDetailInfo {
    @Nullable
    public Listener listeners;
    @Nullable
    public Channel channels;
    @Nullable
    public RadioAudioInfo current;
    @Nullable
    public RadioSchedulingInfo scheduling;
    //打开页面是否自动播放
    public boolean autoPlay;
    @Nullable
    public String welcomeAudio;

    @Nullable
    public String myLikeCover;

    @Nullable
    public String myLikeCoverBlur;
    @Nullable
    public String title;

    public static class Response extends BaseResponse<RadioDetailInfo> {

    }


    public class Listener {
        @Nullable
        public List<UserInfo> list;
        @Nullable
        public String tips;
    }

    public class Channel {
        @Nullable
        public List<Channels> list;
    }

    public static class Channels {
        public int channelId;
        @Nullable
        public String name;
        @Nullable
        public String cover;
        @Nullable
        public String coverBlur;
    }


}
