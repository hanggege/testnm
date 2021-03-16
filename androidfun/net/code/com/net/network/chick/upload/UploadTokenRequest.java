package com.net.network.chick.upload;

import com.mei.orc.http.retrofit.RxRequest;
import com.net.ApiInterface;
import com.net.model.chick.upload.UploadToken;

import io.reactivex.Observable;

/**
 * Created by steven on 15/4/27.
 */
public class UploadTokenRequest extends RxRequest<UploadToken.Response, ApiInterface> {
    private int login_user_id;
    private String session_id;
    private String suffix;

    public UploadTokenRequest(int login_user_id, String session_id, String suffix) {
        super(UploadToken.Response.class, ApiInterface.class);
        this.login_user_id = login_user_id;
        this.session_id = session_id;
        this.suffix = suffix;
    }

    @Override
    public String toString() {
        return "UploadTokenRequest{" +
                "login_user_id=" + login_user_id +
                ", session_id='" + session_id + '\'' +
                '}';
    }

    @Override
    protected String createCacheKey() {
        return toString();
    }

    @Override
    public Observable<UploadToken.Response> loadDataFromNetwork() throws Exception {
        return getService().uploadToken(login_user_id, session_id, suffix);
    }
}
