package com.net.network.chick.course;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.course.AudioGet;

import io.reactivex.Observable;

/**
 * 佛祖保佑         永无BUG
 *
 * @author Created by joker on 2020/7/8
 */
public class AudioGetRequest extends RxRequest<AudioGet.Response, ApiInterface> {

    public int audioId;
    public int courseId;// 当前音频所在的courseid
    public int direction;//-1 上一条， 1下一条

    public AudioGetRequest(int audioId) {
        super(AudioGet.Response.class, ApiInterface.class);
        this.audioId = audioId;
    }

    public AudioGetRequest(int courseId, int audioId, int direction) {
        super(AudioGet.Response.class, ApiInterface.class);
        this.courseId = courseId;
        this.audioId = audioId;
        this.direction = direction;
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    protected Observable<AudioGet.Response> loadDataFromNetwork() throws Exception {
        return getService().AudioGet(audioId, courseId, direction);
    }
}
