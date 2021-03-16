package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.radio.RadioAudioInfo;

import io.reactivex.Observable;

/**
 * @author Created by lenna on 2020/9/23
 * 下一首音频请求
 */
public class RadioNextAudioInfoRequest extends RxRequest<RadioAudioInfo.Response, ApiInterface> {
    private int channelId;
    private int audioId;
    //  mode （order 按专家的顺序播放，random 专辑内随机播放）
    private int direction;

    public RadioNextAudioInfoRequest(int channelId, int audioId, int direction) {
        super(RadioAudioInfo.Response.class, ApiInterface.class);
        this.channelId = channelId;
        this.audioId = audioId;
        this.direction = direction;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RadioAudioInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().getNextAudioInfo(channelId, audioId, direction);
    }
}
