package com.net.network.radio;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.radio.RadioShowLikeInfo;

import io.reactivex.Observable;

/**
 * RadioLikeRequest
 *
 * @author kun
 * @version 1.0.0
 * @since 2020-09-23
 */
public class RadioLikeRequest extends RxRequest<RadioShowLikeInfo.Response, ApiInterface> {

    private int audio;
    private boolean dislike;

    public RadioLikeRequest(int audio, boolean dislike) {
        super(RadioShowLikeInfo.Response.class, ApiInterface.class);
        this.audio = audio;
        this.dislike = dislike;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<RadioShowLikeInfo.Response> loadDataFromNetwork() throws Exception {
        return getService().likeRadio(audio, dislike);
    }
}
