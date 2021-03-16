package com.net.network.chick.course;

import com.mei.orc.http.retrofit.RxRequest;
import com.mei.orc.john.model.Empty_data;
import com.net.ApiInterface;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/9
 */
public class AudioProgressRequest extends RxRequest<Empty_data.Response, ApiInterface> {
    private int courseId;
    private int audioId;
    private int progress;

    public AudioProgressRequest(int courseId, int audioId, int progress) {
        super(Empty_data.Response.class, ApiInterface.class);
        this.courseId = courseId;
        this.audioId = audioId;
        this.progress = progress;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<Empty_data.Response> loadDataFromNetwork() throws Exception {
        return getService().AudioProgress(courseId, audioId, progress);
    }
}
