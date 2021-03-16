package com.net.model.radio;

import com.mei.orc.http.response.BaseResponse;

import androidx.annotation.Nullable;

/**
 * @author Created by lenna on 2020/9/23
 */
public class RadioAudioInfo {
    public int channelId;
    public int audioId;
    @Nullable
    public String title;
    //当前音频url
    @Nullable
    public String url;

    public boolean liked;

    public long playTime;

    public long duration;

    public String serialNum;

    public static class Response extends BaseResponse<RadioAudioInfo> {

    }
}
